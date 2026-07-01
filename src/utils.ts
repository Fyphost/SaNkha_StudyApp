export function formatBytes(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return `${(bytes / Math.pow(k, i)).toFixed(i === 0 ? 0 : 1)} ${sizes[i]}`
}

export function fileIcon(mime: string, name: string): string {
  const ext = name.split('.').pop()?.toLowerCase() ?? ''
  if (mime.startsWith('image/')) return '\uD83D\uDDBC\uFE0F'
  if (mime.startsWith('video/')) return '\uD83C\uDFAC'
  if (mime.startsWith('audio/')) return '\uD83C\uDFB5'
  if (mime === 'application/pdf' || ext === 'pdf') return '\uD83D\uDCC4'
  if (['doc', 'docx'].includes(ext)) return '\uD83D\uDCDD'
  if (['xls', 'xlsx', 'csv'].includes(ext)) return '\uD83D\uDCCA'
  if (['ppt', 'pptx'].includes(ext)) return '\uD83D\uDCFD\uFE0F'
  if (['zip', 'rar', '7z'].includes(ext)) return '\uD83D\uDDDC\uFE0F'
  if (mime.startsWith('text/') || ['txt', 'md'].includes(ext)) return '\uD83D\uDCC3'
  return '\uD83D\uDCCE'
}

export function isPreviewable(mime: string): boolean {
  return (
    mime.startsWith('image/') ||
    mime === 'application/pdf' ||
    mime.startsWith('text/') ||
    mime.startsWith('video/') ||
    mime.startsWith('audio/')
  )
}

export const QUOTES = [
  "It's not over until I win.",
  'Stay Focused \u2014 Keep Growing.',
  'Dream. Plan. Achieve.',
  'Believe in Yourself.',
  'Good Vibes, Good Life.',
  'Discipline beats motivation.',
  'One page at a time.',
]
