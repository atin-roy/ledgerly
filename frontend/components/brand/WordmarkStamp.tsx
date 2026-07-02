/**
 * Ledgerly wordmark — a rubber-stamp brand mark.
 * Final design asset. To swap in a hand-drawn version later, replace this SVG
 * (or point an <img> at your own file). Ink color comes from `currentColor`.
 */
export default function WordmarkStamp({
  className,
}: {
  className?: string;
}) {
  return (
    <svg
      className={className}
      viewBox="0 0 264 96"
      role="img"
      aria-label="Ledgerly"
      fill="none"
    >
      <defs>
        <filter id="wordmark-ink" x="-6%" y="-15%" width="112%" height="130%">
          <feTurbulence
            type="fractalNoise"
            baseFrequency="0.85"
            numOctaves="2"
            seed="7"
            result="noise"
          />
          <feDisplacementMap
            in="SourceGraphic"
            in2="noise"
            scale="1.4"
            xChannelSelector="R"
            yChannelSelector="G"
          />
        </filter>
      </defs>

      <g
        filter="url(#wordmark-ink)"
        stroke="currentColor"
        fill="currentColor"
        opacity="0.96"
      >
        <rect
          x="3"
          y="3"
          width="258"
          height="90"
          rx="5"
          fill="none"
          strokeWidth="3"
        />
        <rect
          x="9.5"
          y="9.5"
          width="245"
          height="77"
          rx="2.5"
          fill="none"
          strokeWidth="1"
        />
        <text
          x="132"
          y="52"
          textAnchor="middle"
          stroke="none"
          style={{
            fontFamily: "var(--font-display), Georgia, serif",
            fontWeight: 700,
            fontSize: "33px",
            letterSpacing: "7px",
          }}
        >
          LEDGERLY
        </text>
        <line x1="88" y1="63" x2="176" y2="63" strokeWidth="0.75" opacity="0.65" />
        <text
          x="132"
          y="79"
          textAnchor="middle"
          stroke="none"
          style={{
            fontFamily: "var(--font-body), system-ui, sans-serif",
            fontWeight: 600,
            fontSize: "9px",
            letterSpacing: "6px",
          }}
        >
          PERSONAL LEDGER
        </text>
      </g>
    </svg>
  );
}
