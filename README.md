# SanKhadip — Study App

> _It's not over until I win._

A dark, neon-blue study workspace to organize your notes and study material by
subject. Built with **React + TypeScript + Vite**, with everything stored
locally in your browser via **IndexedDB** (works offline, nothing leaves your
device).

## Features

- **5 fixed subject folders**: Physics, Chemistry, Maths, Biology, English
  (these can't be deleted or renamed — they're your base subjects).
- **Nested folders** — create and delete subfolders inside any subject
  (e.g. `Physics → Vector Physics`), as deep as you like.
- **File management** — upload files (drag & drop or picker), rename, delete,
  download, and preview.
- **In-app preview** for images, PDFs, video, audio, and text files.
- **Search** within the current folder.
- **Rotating motivational quotes** in the header.
- **Persistent storage** — your folders and files survive page reloads.

## Getting started

```bash
npm install
npm run dev      # start the dev server
npm run build    # production build
npm run preview  # preview the production build
```

## Using your own logo

The header shows an SVG recreation of the SanKhadip badge by default. To use the
real logo, drop your image at **`public/logo.png`** and it will be picked up
automatically.

## Tech notes

- Data model: a single IndexedDB object store (`nodes`) holding a tree of
  folder/file records. Files are stored as `Blob`s alongside their metadata.
- No backend required — this is a fully client-side app.
