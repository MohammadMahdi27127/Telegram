"use client";

import { useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { Check, Copy } from "lucide-react";

type CopyRowProps = {
  /** مقداری که در کلیپ‌بورد کپی می‌شود (لاتین و بدون فاصله). */
  value: string;
  /** متنی که روی صفحه نمایش داده می‌شود. */
  display: string;
  /** روشن برای پس‌زمینه‌های سفید، تیره برای کارت‌های گرادیانی. */
  tone?: "dark" | "light";
  className?: string;
  displayClassName?: string;
};

/** جعبهٔ نمایش مقدار + دکمهٔ کپی تک‌کلیکی همراه با بازخورد شناور. */
export function CopyRow({
  value,
  display,
  tone = "dark",
  className = "",
  displayClassName = "",
}: CopyRowProps) {
  const [copied, setCopied] = useState(false);

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(value);
    } catch {
      const area = document.createElement("textarea");
      area.value = value;
      area.style.position = "fixed";
      area.style.opacity = "0";
      document.body.appendChild(area);
      area.select();
      document.execCommand("copy");
      document.body.removeChild(area);
    }
    setCopied(true);
    window.setTimeout(() => setCopied(false), 1800);
  };

  const buttonTone =
    tone === "dark"
      ? "border-white/40 bg-white/15 text-white/90 hover:bg-white/25"
      : "border-slate-200 bg-white text-ink-soft hover:border-brand-200 hover:text-brand-700";

  return (
    <div className={`relative flex items-center gap-2 ${className}`}>
      <span className={`flex-1 select-all tabular-nums ${displayClassName}`} dir="ltr">
        {display}
      </span>

      <button
        type="button"
        onClick={copy}
        aria-label="کپی در کلیپ‌بورد"
        className={`relative inline-flex h-9 w-9 shrink-0 items-center justify-center rounded-xl border transition ${buttonTone}`}
      >
        {copied ? <Check className="h-4 w-4" /> : <Copy className="h-4 w-4" />}
      </button>

      <AnimatePresence>
        {copied && (
          <motion.span
            initial={{ opacity: 0, y: 6, scale: 0.96 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 6, scale: 0.96 }}
            transition={{ duration: 0.18 }}
            className="pointer-events-none absolute -top-10 left-0 z-10 rounded-xl bg-ink px-3 py-1.5 text-[11px] font-medium text-white shadow-lift"
          >
            کپی شد
          </motion.span>
        )}
      </AnimatePresence>
    </div>
  );
}
