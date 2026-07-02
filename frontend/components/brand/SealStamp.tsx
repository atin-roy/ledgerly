/**
 * Ledgerly seal — a circular rubber-stamp mark for statement/summary contexts.
 * Final design asset; swap the SVG to use a hand-drawn version later.
 * Ink color comes from `currentColor`.
 */
export default function SealStamp({ className }: { className?: string }) {
  return (
    <svg
      className={className}
      viewBox="0 0 200 200"
      role="img"
      aria-label="Ledgerly seal"
      fill="none"
    >
      <defs>
        <filter id="seal-ink" x="-8%" y="-8%" width="116%" height="116%">
          <feTurbulence
            type="fractalNoise"
            baseFrequency="0.8"
            numOctaves="2"
            seed="4"
            result="noise"
          />
          <feDisplacementMap
            in="SourceGraphic"
            in2="noise"
            scale="1.5"
            xChannelSelector="R"
            yChannelSelector="G"
          />
        </filter>
        <path id="seal-top" d="M30,100 a70,70 0 0 1 140,0" />
        <path id="seal-bottom" d="M34,104 a66,66 0 0 0 132,0" />
      </defs>

      <g
        filter="url(#seal-ink)"
        stroke="currentColor"
        fill="currentColor"
        opacity="0.95"
      >
        {/* rings */}
        <circle cx="100" cy="100" r="94" fill="none" strokeWidth="2.5" />
        <circle cx="100" cy="100" r="80" fill="none" strokeWidth="1" />

        {/* curved text */}
        <text
          stroke="none"
          style={{
            fontFamily: "var(--font-display), Georgia, serif",
            fontWeight: 700,
            fontSize: "15px",
            letterSpacing: "3px",
          }}
        >
          <textPath href="#seal-top" startOffset="50%" textAnchor="middle">
            LEDGERLY
          </textPath>
        </text>
        <text
          stroke="none"
          style={{
            fontFamily: "var(--font-body), system-ui, sans-serif",
            fontWeight: 600,
            fontSize: "9px",
            letterSpacing: "3.5px",
          }}
        >
          <textPath href="#seal-bottom" startOffset="50%" textAnchor="middle">
            KEPT IN ORDER
          </textPath>
        </text>

        {/* side diamonds where the arcs meet */}
        <path d="M16,100 l4,-4 4,4 -4,4 z" stroke="none" />
        <path d="M184,100 l-4,-4 -4,4 4,4 z" stroke="none" />

        {/* center emblem — stacked ledger rules */}
        <g strokeWidth="2" strokeLinecap="square">
          <line x1="78" y1="93" x2="122" y2="93" />
          <line x1="85" y1="100" x2="115" y2="100" />
          <line x1="78" y1="107" x2="122" y2="107" />
        </g>
      </g>
    </svg>
  );
}
