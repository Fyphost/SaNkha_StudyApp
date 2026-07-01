export type NodeType = 'folder' | 'file'

export interface BaseNode {
  id: string
  parentId: string | null
  name: string
  type: NodeType
  createdAt: number
  updatedAt: number
}

export interface FolderNode extends BaseNode {
  type: 'folder'
  /** true for the fixed subject folders (Physics, Chemistry, ...) which cannot be deleted or renamed */
  subject?: boolean
  /** accent color used for the folder tile */
  color?: string
  /** emoji/icon shown on the folder tile */
  icon?: string
}

export interface FileNode extends BaseNode {
  type: 'file'
  mime: string
  size: number
  blob: Blob
}

export type TreeNode = FolderNode | FileNode

export const SUBJECTS: { name: string; color: string; icon: string }[] = [
  { name: 'Physics', color: '#1e90ff', icon: '\u269B' },
  { name: 'Chemistry', color: '#00d4a0', icon: '\u2697' },
  { name: 'Maths', color: '#a26bff', icon: '\u221A' },
  { name: 'Biology', color: '#ff5c8a', icon: '\uD83E\uDDEC' },
  { name: 'English', color: '#ffb020', icon: '\uD83D\uDCD6' },
]
