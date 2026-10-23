import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./src/**/*.{ts,tsx,mdx}"],
  theme: {
    extend: {
      fontFamily: {
        sans: ["var(--font-vazirmatn)", "system-ui", "sans-serif"],
      },
      colors: {
        brand: {
          50: "#eff8ff",
          100: "#daeeff",
          200: "#bee1ff",
          300: "#91cfff",
          400: "#5db4fb",
          500: "#229ed9",
          600: "#1788c0",
          700: "#146d9b",
          800: "#165c80",
          900: "#174d6a",
        },
        plus: {
          50: "#f5f3ff",
          100: "#ede9fe",
          300: "#c4b5fd",
          500: "#8b5cf6",
          600: "#7c3aed",
          700: "#6d28d9",
        },
        ink: {
          DEFAULT: "#0b1220",
          soft: "#334155",
          muted: "#64748b",
          faint: "#94a3b8",
        },
        canvas: "#f4f7fd",
      },
      boxShadow: {
        soft: "0 1px 2px rgba(15, 23, 42, 0.04), 0 8px 24px -12px rgba(15, 23, 42, 0.12)",
        lift: "0 24px 60px -28px rgba(15, 23, 42, 0.28)",
        glow: "0 18px 46px -18px rgba(34, 158, 217, 0.55)",
        plus: "0 22px 50px -22px rgba(124, 58, 237, 0.6)",
      },
      keyframes: {
        marquee: {
          "0%": { transform: "translateX(0)" },
          "100%": { transform: "translateX(-50%)" },
        },
        floaty: {
          "0%, 100%": { transform: "translateY(0)" },
          "50%": { transform: "translateY(-10px)" },
        },
        pulseRing: {
          "0%": { transform: "scale(0.9)", opacity: "0.7" },
          "100%": { transform: "scale(1.6)", opacity: "0" },
        },
      },
      animation: {
        marquee: "marquee 32s linear infinite",
        floaty: "floaty 6s ease-in-out infinite",
        pulseRing: "pulseRing 2.4s ease-out infinite",
      },
    },
  },
  plugins: [],
};

export default config;
