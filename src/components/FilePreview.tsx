import { useEffect, useState } from 'react'
import type { FileNode } from '../types'
import { formatBytes, isPreviewable } from '../utils'

export default function FilePreview({ file }: { file: FileNode }) {
  const [url, setUrl] = useState<string>('')
  const [text, setText] = useState<string>('')

  useEffect(() => {
    const objectUrl = URL.createObjectURL(file.blob)
    setUrl(objectUrl)
    if (file.mime.startsWith('text/')) {
      file.blob.text().then((t) => setText(t.slice(0, 20000)))
    }
    return () => URL.revokeObjectURL(objectUrl)
  }, [file])

  if (!url) return null

  const download = () => {
    const a = document.createElement('a')
    a.href = url
    a.download = file.name
    a.click()
  }

  return (
    <div className="preview">
      <div className="preview-stage">
        {file.mime.startsWith('image/') && (
          <img src={url} alt={file.name} />
        )}
        {file.mime === 'application/pdf' && (
          <iframe src={url} title={file.name} className="preview-frame" />
        )}
        {file.mime.startsWith('video/') && (
          <video src={url} controls className="preview-frame" />
        )}
        {file.mime.startsWith('audio/') && <audio src={url} controls />}
        {file.mime.startsWith('text/') && (
          <pre className="preview-text">{text}</pre>
        )}
        {!isPreviewable(file.mime) && (
          <div className="preview-none">
            <span className="preview-none-icon">&#x1F4CE;</span>
            <p>No preview available for this file type.</p>
          </div>
        )}
      </div>
      <div className="preview-footer">
        <span className="muted">
          {file.mime || 'unknown'} &middot; {formatBytes(file.size)}
        </span>
        <button className="btn btn-primary" onClick={download}>
          &#x2B07; Download
        </button>
      </div>
    </div>
  )
}
