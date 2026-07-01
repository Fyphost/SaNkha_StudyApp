import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import {
  createFileNode,
  createFolder,
  deleteNodeDeep,
  ensureSeeded,
  getAllNodes,
  putNode,
} from './db'
import type { FolderNode, TreeNode } from './types'

interface StoreValue {
  nodes: TreeNode[]
  loading: boolean
  childrenOf: (parentId: string | null) => TreeNode[]
  getById: (id: string) => TreeNode | undefined
  pathTo: (id: string | null) => FolderNode[]
  addFolder: (parentId: string | null, name: string) => Promise<void>
  addFiles: (parentId: string, files: FileList | File[]) => Promise<void>
  rename: (id: string, name: string) => Promise<void>
  remove: (id: string) => Promise<void>
}

const StoreContext = createContext<StoreValue | null>(null)

export function StoreProvider({ children }: { children: ReactNode }) {
  const [nodes, setNodes] = useState<TreeNode[]>([])
  const [loading, setLoading] = useState(true)

  const reload = useCallback(async () => {
    const all = await getAllNodes()
    setNodes(all)
  }, [])

  useEffect(() => {
    ;(async () => {
      await ensureSeeded()
      await reload()
      setLoading(false)
    })()
  }, [reload])

  const childrenOf = useCallback(
    (parentId: string | null) =>
      nodes
        .filter((n) => n.parentId === parentId)
        .sort((a, b) => {
          // folders first, then alphabetical
          if (a.type !== b.type) return a.type === 'folder' ? -1 : 1
          return a.name.localeCompare(b.name, undefined, { numeric: true })
        }),
    [nodes],
  )

  const getById = useCallback(
    (id: string) => nodes.find((n) => n.id === id),
    [nodes],
  )

  const pathTo = useCallback(
    (id: string | null): FolderNode[] => {
      const path: FolderNode[] = []
      let current = id ? nodes.find((n) => n.id === id) : undefined
      while (current && current.type === 'folder') {
        path.unshift(current)
        current = current.parentId
          ? nodes.find((n) => n.id === current!.parentId)
          : undefined
      }
      return path
    },
    [nodes],
  )

  const addFolder = useCallback(
    async (parentId: string | null, name: string) => {
      await putNode(createFolder(parentId, name.trim() || 'New Folder'))
      await reload()
    },
    [reload],
  )

  const addFiles = useCallback(
    async (parentId: string, files: FileList | File[]) => {
      for (const file of Array.from(files)) {
        const node = await createFileNode(parentId, file)
        await putNode(node)
      }
      await reload()
    },
    [reload],
  )

  const rename = useCallback(
    async (id: string, name: string) => {
      const node = nodes.find((n) => n.id === id)
      if (!node) return
      const clean = name.trim()
      if (!clean) return
      await putNode({ ...node, name: clean, updatedAt: Date.now() })
      await reload()
    },
    [nodes, reload],
  )

  const remove = useCallback(
    async (id: string) => {
      await deleteNodeDeep(id)
      await reload()
    },
    [reload],
  )

  const value = useMemo<StoreValue>(
    () => ({
      nodes,
      loading,
      childrenOf,
      getById,
      pathTo,
      addFolder,
      addFiles,
      rename,
      remove,
    }),
    [nodes, loading, childrenOf, getById, pathTo, addFolder, addFiles, rename, remove],
  )

  return <StoreContext.Provider value={value}>{children}</StoreContext.Provider>
}

export function useStore(): StoreValue {
  const ctx = useContext(StoreContext)
  if (!ctx) throw new Error('useStore must be used within StoreProvider')
  return ctx
}
