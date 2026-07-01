import { useEffect, useMemo, useRef, useState, type DragEvent } from 'react'
import './App.css'
import Logo from './components/Logo'
import Modal from './components/Modal'
import FilePreview from './components/FilePreview'
import { useStore } from './store'
import type { FileNode, FolderNode, TreeNode } from './types'
import { fileIcon, formatBytes, QUOTES } from './utils'

type Dialog =
  | { kind: 'newFolder'; parentId: string | null }
  | { kind: 'rename'; node: TreeNode }
  | { kind: 'confirmDelete'; node: TreeNode }
  | { kind: 'preview'; node: FileNode }
  | null

export default function App() {
  const store = useStore()
  const [currentId, setCurrentId] = useState<string | null>(null)
  const [dialog, setDialog] = useState<Dialog>(null)
  const [dragOver, setDragOver] = useState(false)
  const [quoteIdx, setQuoteIdx] = useState(0)
  const [search, setSearch] = useState('')
  const fileInputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    const t = setInterval(
      () => setQuoteIdx((i) => (i + 1) % QUOTES.length),
      5000,
    )
    return () => clearInterval(t)
  }, [])

  const subjects = useMemo(
    () => store.childrenOf(null) as FolderNode[],
    [store],
  )
  const path = store.pathTo(currentId)
  const current = currentId ? (store.getById(currentId) as FolderNode) : null
  const items = store.childrenOf(currentId)
  const filtered = search
    ? items.filter((n) =>
        n.name.toLowerCase().includes(search.toLowerCase()),
      )
    : items

  const openItem = (node: TreeNode) => {
    if (node.type === 'folder') {
      setCurrentId(node.id)
      setSearch('')
    } else {
      setDialog({ kind: 'preview', node })
    }
  }

  const handleUploadClick = () => fileInputRef.current?.click()

  const onFilesPicked = async (files: FileList | null) => {
    if (!files || !currentId) return
    await store.addFiles(currentId, files)
  }

  const onDrop = async (e: DragEvent) => {
    e.preventDefault()
    setDragOver(false)
    if (!currentId) return
    if (e.dataTransfer.files.length) {
      await store.addFiles(currentId, e.dataTransfer.files)
    }
  }

  const downloadFile = (file: FileNode) => {
    const url = URL.createObjectURL(file.blob)
    const a = document.createElement('a')
    a.href = url
    a.download = file.name
    a.click()
    URL.revokeObjectURL(url)
  }

  if (store.loading) {
    return (
      <div className="splash">
        <Logo size={96} />
        <p>Loading SanKhadip{'\u2026'}</p>
      </div>
    )
  }

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand" onClick={() => setCurrentId(null)}>
          <Logo size={44} />
          <div className="brand-text">
            <span className="brand-name">SanKhadip</span>
            <span className="brand-quote" key={quoteIdx}>
              {QUOTES[quoteIdx]}
            </span>
          </div>
        </div>
        <div className="search-box">
          <span>&#x1F50D;</span>
          <input
            placeholder={'Search in this folder\u2026'}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </header>

      <div className="layout">
        <aside className="sidebar">
          <button
            className={`nav-item ${currentId === null ? 'active' : ''}`}
            onClick={() => setCurrentId(null)}
          >
            <span className="nav-icon">&#x1F3E0;</span> Home
          </button>
          <div className="nav-label">Subjects</div>
          {subjects.map((s) => (
            <button
              key={s.id}
              className={`nav-item ${path[0]?.id === s.id ? 'active' : ''}`}
              onClick={() => setCurrentId(s.id)}
            >
              <span className="nav-icon" style={{ color: s.color }}>
                {s.icon ?? '\uD83D\uDCC1'}
              </span>
              {s.name}
            </button>
          ))}
        </aside>

        <main
          className={`content ${dragOver ? 'drag-over' : ''}`}
          onDragOver={(e) => {
            if (currentId) {
              e.preventDefault()
              setDragOver(true)
            }
          }}
          onDragLeave={() => setDragOver(false)}
          onDrop={onDrop}
        >
          <div className="breadcrumbs">
            <button className="crumb" onClick={() => setCurrentId(null)}>
              Home
            </button>
            {path.map((p) => (
              <span key={p.id} className="crumb-wrap">
                <span className="crumb-sep">/</span>
                <button className="crumb" onClick={() => setCurrentId(p.id)}>
                  {p.name}
                </button>
              </span>
            ))}
          </div>

          <div className="toolbar">
            <h2 className="view-title">
              {current ? (
                <>
                  <span style={{ color: path[0]?.color }}>
                    {current.icon ?? '\uD83D\uDCC1'}
                  </span>{' '}
                  {current.name}
                </>
              ) : (
                'Your Subjects'
              )}
            </h2>
            <div className="toolbar-actions">
              {currentId && (
                <>
                  <button className="btn" onClick={handleUploadClick}>
                    &#x2B06; Upload files
                  </button>
                  <button
                    className="btn btn-primary"
                    onClick={() =>
                      setDialog({ kind: 'newFolder', parentId: currentId })
                    }
                  >
                    &#x2795; New folder
                  </button>
                </>
              )}
            </div>
          </div>

          <input
            ref={fileInputRef}
            type="file"
            multiple
            hidden
            onChange={(e) => {
              onFilesPicked(e.target.files)
              e.target.value = ''
            }}
          />

          {filtered.length === 0 ? (
            <div className="empty">
              <span className="empty-icon">
                {currentId ? '\uD83D\uDCED' : '\uD83D\uDCDA'}
              </span>
              <p>
                {currentId
                  ? 'This folder is empty. Create a folder or upload files to begin.'
                  : 'Pick a subject to start studying.'}
              </p>
              {currentId && (
                <div className="empty-actions">
                  <button className="btn" onClick={handleUploadClick}>
                    Upload files
                  </button>
                  <button
                    className="btn btn-primary"
                    onClick={() =>
                      setDialog({ kind: 'newFolder', parentId: currentId })
                    }
                  >
                    New folder
                  </button>
                </div>
              )}
            </div>
          ) : (
            <div className="grid">
              {filtered.map((node) => (
                <ItemCard
                  key={node.id}
                  node={node}
                  accent={path[0]?.color}
                  onOpen={() => openItem(node)}
                  onRename={() => setDialog({ kind: 'rename', node })}
                  onDelete={() =>
                    setDialog({ kind: 'confirmDelete', node })
                  }
                  onDownload={() =>
                    node.type === 'file' && downloadFile(node)
                  }
                />
              ))}
            </div>
          )}

          {dragOver && (
            <div className="drop-hint">Drop files to upload here</div>
          )}
        </main>
      </div>

      {dialog?.kind === 'newFolder' && (
        <InputDialog
          title="New folder"
          label="Folder name"
          placeholder="e.g. Vector Physics"
          confirmText="Create"
          onCancel={() => setDialog(null)}
          onConfirm={async (name) => {
            await store.addFolder(dialog.parentId, name)
            setDialog(null)
          }}
        />
      )}

      {dialog?.kind === 'rename' && (
        <InputDialog
          title={`Rename ${dialog.node.type}`}
          label="New name"
          initial={dialog.node.name}
          confirmText="Rename"
          onCancel={() => setDialog(null)}
          onConfirm={async (name) => {
            await store.rename(dialog.node.id, name)
            setDialog(null)
          }}
        />
      )}

      {dialog?.kind === 'confirmDelete' && (
        <Modal title="Delete" onClose={() => setDialog(null)}>
          <p>
            Delete <strong>{dialog.node.name}</strong>
            {dialog.node.type === 'folder'
              ? ' and everything inside it'
              : ''}
            ? This cannot be undone.
          </p>
          <div className="dialog-actions">
            <button className="btn" onClick={() => setDialog(null)}>
              Cancel
            </button>
            <button
              className="btn btn-danger"
              onClick={async () => {
                await store.remove(dialog.node.id)
                setDialog(null)
              }}
            >
              Delete
            </button>
          </div>
        </Modal>
      )}

      {dialog?.kind === 'preview' && (
        <Modal
          title={dialog.node.name}
          onClose={() => setDialog(null)}
          wide
        >
          <FilePreview file={dialog.node} />
        </Modal>
      )}
    </div>
  )
}

function ItemCard({
  node,
  accent,
  onOpen,
  onRename,
  onDelete,
  onDownload,
}: {
  node: TreeNode
  accent?: string
  onOpen: () => void
  onRename: () => void
  onDelete: () => void
  onDownload: () => void
}) {
  const isFolder = node.type === 'folder'
  const locked = isFolder && (node as FolderNode).subject
  const folderAccent = isFolder
    ? (node as FolderNode).color ?? accent ?? '#1e90ff'
    : accent ?? '#1e90ff'

  return (
    <div
      className={`card ${isFolder ? 'card-folder' : 'card-file'}`}
      style={{ ['--accent' as string]: folderAccent }}
      onDoubleClick={onOpen}
    >
      <button className="card-main" onClick={onOpen}>
        <span className="card-icon">
          {isFolder
            ? (node as FolderNode).icon ?? '\uD83D\uDCC1'
            : fileIcon((node as FileNode).mime, node.name)}
        </span>
        <span className="card-name" title={node.name}>
          {node.name}
        </span>
        <span className="card-meta">
          {isFolder
            ? 'Folder'
            : formatBytes((node as FileNode).size)}
        </span>
      </button>
      <div className="card-actions">
        {node.type === 'file' && (
          <button
            className="icon-btn"
            title="Download"
            onClick={(e) => {
              e.stopPropagation()
              onDownload()
            }}
          >
            &#x2B07;
          </button>
        )}
        {!locked && (
          <>
            <button
              className="icon-btn"
              title="Rename"
              onClick={(e) => {
                e.stopPropagation()
                onRename()
              }}
            >
              &#x270E;
            </button>
            <button
              className="icon-btn danger"
              title="Delete"
              onClick={(e) => {
                e.stopPropagation()
                onDelete()
              }}
            >
              &#x1F5D1;
            </button>
          </>
        )}
      </div>
    </div>
  )
}

function InputDialog({
  title,
  label,
  placeholder,
  initial = '',
  confirmText,
  onConfirm,
  onCancel,
}: {
  title: string
  label: string
  placeholder?: string
  initial?: string
  confirmText: string
  onConfirm: (value: string) => void
  onCancel: () => void
}) {
  const [value, setValue] = useState(initial)
  const ref = useRef<HTMLInputElement>(null)

  useEffect(() => {
    ref.current?.focus()
    ref.current?.select()
  }, [])

  const submit = () => {
    if (value.trim()) onConfirm(value.trim())
  }

  return (
    <Modal title={title} onClose={onCancel}>
      <label className="field-label">{label}</label>
      <input
        ref={ref}
        className="text-input"
        value={value}
        placeholder={placeholder}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={(e) => e.key === 'Enter' && submit()}
      />
      <div className="dialog-actions">
        <button className="btn" onClick={onCancel}>
          Cancel
        </button>
        <button
          className="btn btn-primary"
          onClick={submit}
          disabled={!value.trim()}
        >
          {confirmText}
        </button>
      </div>
    </Modal>
  )
}
