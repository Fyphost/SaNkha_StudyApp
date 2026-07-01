import { useState } from 'react'

/**
 * Circular neon "SANKHADIP" badge. If a real logo image is placed at
 * /logo.png (in the public folder) it will be used instead of the SVG.
 */
export default function Logo({ size = 48 }: { size?: number }) {
  const [useImg, setUseImg] = useState(true)

  if (useImg) {
    return (
      <img
        src="/logo.png"
        width={size}
        height={size}
        alt="SanKhadip"
        className="logo-img"
        onError={() => setUseImg(false)}
      />
    )
  }

  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 100 100"
      className="logo-svg"
      role="img"
      aria-label="SanKhadip logo"
    >
      <defs>
        <radialGradient id="glow" cx="50%" cy="45%" r="60%">
          <stop offset="0%" stopColor="#0a2a55" />
          <stop offset="100%" stopColor="#02040a" />
        </radialGradient>
        <linearGradient id="ink" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" stopColor="#7fd0ff" />
          <stop offset="100%" stopColor="#1e90ff" />
        </linearGradient>
      </defs>
      <circle cx="50" cy="50" r="48" fill="url(#glow)" stroke="#1e90ff" strokeWidth="1.5" />
      <circle cx="50" cy="50" r="44" fill="none" stroke="#0e4b8a" strokeWidth="0.6" />
      <text
        x="50"
        y="60"
        textAnchor="middle"
        fontSize="15"
        fontWeight="800"
        fill="url(#ink)"
        fontFamily="'Segoe UI', sans-serif"
        style={{ letterSpacing: '0.5px' }}
      >
        SANKHA
      </text>
      <text
        x="50"
        y="74"
        textAnchor="middle"
        fontSize="15"
        fontWeight="800"
        fill="url(#ink)"
        fontFamily="'Segoe UI', sans-serif"
      >
        DIP
      </text>
      <path
        d="M22 30 l10 4 -10 4 2 -4 z"
        fill="#bfe6ff"
        opacity="0.9"
      />
    </svg>
  )
}
