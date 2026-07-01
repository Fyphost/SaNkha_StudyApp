import type { FileNode, FolderNode, TreeNode } from './types'
import { SUBJECTS } from './types'

const DB_NAME = 'sankhadip-db'
const DB_VERSION = 1
const STORE = 'nodes'

let dbPromise: Promise<IDBDatabase> | null = null

function openDB(): Promise<IDBDatabase> {
  if (dbPromise) return dbPromise
  dbPromise = new Promise((resolve, reject) => {
    const req = indexedDB.open(DB_NAME, DB_VERSION)
    req.onupgradeneeded = () => {
      const db = req.result
      if (!db.objectStoreNames.contains(STORE)) {
        const store = db.createObjectStore(STORE, { keyPath: 'id' })
        store.createIndex('parentId', 'parentId', { unique: false })
      }
    }
    req.onsuccess = () => resolve(req.result)
    req.onerror = () => reject(req.error)
  })
  return dbPromise
}

function tx(mode: IDBTransactionMode): Promise<IDBObjectStore> {
  return openDB().then((db) => db.transaction(STORE, mode).objectStore(STORE))
}

function reqToPromise<T>(request: IDBRequest<T>): Promise<T> {
  return new Promise((resolve, reject) => {
    request.onsuccess = () => resolve(request.result)
    request.onerror = () => reject(request.error)
  })
}

export function uid(): string {
  return (
    Date.now().toString(36) + Math.random().toString(36).slice(2, 10)
  )
}

export async function getAllNodes(): Promise<TreeNode[]> {
  const store = await tx('readonly')
  return reqToPromise(store.getAll() as IDBRequest<TreeNode[]>)
}

export async function putNode(node: TreeNode): Promise<void> {
  const store = await tx('readwrite')
  await reqToPromise(store.put(node))
}

export async function getNode(id: string): Promise<TreeNode | undefined> {
  const store = await tx('readonly')
  return reqToPromise(store.get(id) as IDBRequest<TreeNode | undefined>)
}

/** Recursively delete a node and all of its descendants. */
export async function deleteNodeDeep(id: string): Promise<void> {
  const all = await getAllNodes()
  const toDelete = new Set<string>()
  const collect = (nodeId: string) => {
    toDelete.add(nodeId)
    for (const n of all) {
      if (n.parentId === nodeId) collect(n.id)
    }
  }
  collect(id)
  const store = await tx('readwrite')
  await Promise.all([...toDelete].map((d) => reqToPromise(store.delete(d))))
}

export function createFolder(
  parentId: string | null,
  name: string,
  extra: Partial<FolderNode> = {},
): FolderNode {
  const now = Date.now()
  return {
    id: uid(),
    parentId,
    name,
    type: 'folder',
    createdAt: now,
    updatedAt: now,
    ...extra,
  }
}

export async function createFileNode(
  parentId: string,
  file: File,
): Promise<FileNode> {
  const now = Date.now()
  return {
    id: uid(),
    parentId,
    name: file.name,
    type: 'file',
    mime: file.type || 'application/octet-stream',
    size: file.size,
    blob: file,
    createdAt: now,
    updatedAt: now,
  }
}

/** Seed the 5 fixed subject folders on first launch. */
export async function ensureSeeded(): Promise<void> {
  const all = await getAllNodes()
  const hasSubjects = all.some(
    (n) => n.type === 'folder' && (n as FolderNode).subject,
  )
  if (hasSubjects) return
  for (const s of SUBJECTS) {
    await putNode(
      createFolder(null, s.name, {
        subject: true,
        color: s.color,
        icon: s.icon,
      }),
    )
  }
}
