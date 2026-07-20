/**
 * Ledgerly seal — a circular rubber-stamp mark for statement/summary contexts.
 * Designed to read as red ink pressed onto paper: pass the ink color via
 * `currentColor` and let the parent set blend/rotation.
 */
import { useId } from "react";

export default function SealStamp({ className }: { className?: string }) {
  const uid = useId();
  const inkId = `seal-ink${uid}`;
  const topId = `seal-top${uid}`;
  const bottomId = `seal-bottom${uid}`;
  return (
    <svg
      className={className}
      viewBox="0 0 200 200"
      role="img"
      aria-label="Ledgerly seal — kept in order"
      fill="none"
    >
      <defs>
        <filter id={inkId} x="-8%" y="-8%" width="116%" height="116%">
          <feTurbulence
            type="fractalNoise"
            baseFrequency="0.9"
            numOctaves="2"
            seed="4"
            result="noise"
          />
          <feDisplacementMap
            in="SourceGraphic"
            in2="noise"
            scale="2.2"
            xChannelSelector="R"
            yChannelSelector="G"
          />
        </filter>
        <path id={topId} d="M28,100 a72,72 0 0 1 144,0" />
        <path id={bottomId} d="M36,102 a64,64 0 0 0 128,0" />
      </defs>

      <g filter={`url(#${inkId})`} stroke="currentColor" fill="currentColor">
        {/* rings */}
        <circle cx="100" cy="100" r="95" fill="none" strokeWidth="5" />
        <circle cx="100" cy="100" r="87" fill="none" strokeWidth="1.5" />
        <circle cx="100" cy="100" r="55" fill="none" strokeWidth="1.5" />

        {/* curved text */}
        <text
          stroke="none"
          style={{
            fontFamily: "var(--font-display), Georgia, serif",
            fontWeight: 700,
            fontSize: "21px",
            letterSpacing: "4px",
          }}
        >
          <textPath href={`#${topId}`} startOffset="50%" textAnchor="middle">
            LEDGERLY
          </textPath>
        </text>
        <text
          stroke="none"
          style={{
            fontFamily: "var(--font-body), system-ui, sans-serif",
            fontWeight: 700,
            fontSize: "12.5px",
            letterSpacing: "4px",
          }}
        >
          <textPath href={`#${bottomId}`} startOffset="50%" textAnchor="middle">
            KEPT IN ORDER
          </textPath>
        </text>

        {/* side stars where the arcs meet */}
        <path d="M22,100 l5,-5 5,5 -5,5 z" stroke="none" />
        <path d="M178,100 l-5,-5 -5,5 5,5 z" stroke="none" />

        {/* center emblem — stacked ledger rules */}
        <g strokeWidth="4" strokeLinecap="square">
          <line x1="76" y1="90" x2="124" y2="90" />
          <line x1="84" y1="100" x2="116" y2="100" />
          <line x1="76" y1="110" x2="124" y2="110" />
        </g>
      </g>
    </svg>
  );
}
