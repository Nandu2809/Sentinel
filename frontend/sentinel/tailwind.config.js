/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        void: {
          DEFAULT: "#0A0D12",
          950: "#06080B",
          900: "#0A0D12",
          800: "#0F1521",
          700: "#141B2E",
          600: "#1B2438",
        },
        line: {
          DEFAULT: "#1E293B",
          soft: "#151D2E",
        },
        ink: {
          DEFAULT: "#E5E9F0",
          muted: "#7C8598",
          dim: "#4B5468",
        },
        signal: {
          safe: "#22C55E",
          warn: "#F5A623",
          critical: "#EF4444",
          intel: "#8B5CF6",
          live: "#22D3EE",
        },
      },
      fontFamily: {
        mono: ["JetBrains Mono", "ui-monospace", "SFMono-Regular", "monospace"],
        sans: ["Inter", "ui-sans-serif", "system-ui", "sans-serif"],
      },
      boxShadow: {
        bracket: "0 0 0 1px rgba(30,41,59,0.6)",
        glow: "0 0 24px -8px currentColor",
      },
      keyframes: {
        heartbeat: {
          "0%, 100%": { opacity: 0.4 },
          "50%": { opacity: 1 },
        },
        scanline: {
          "0%": { transform: "translateY(-100%)" },
          "100%": { transform: "translateY(100%)" },
        },
        "pulse-ring": {
          "0%": { transform: "scale(0.8)", opacity: "0.8" },
          "100%": { transform: "scale(2.2)", opacity: "0" },
        },
      },
      animation: {
        heartbeat: "heartbeat 2.2s ease-in-out infinite",
        scanline: "scanline 3s linear infinite",
        "pulse-ring": "pulse-ring 1.8s cubic-bezier(0.4,0,0.6,1) infinite",
      },
    },
  },
  plugins: [],
};
