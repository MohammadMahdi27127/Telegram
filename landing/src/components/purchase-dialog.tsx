"use client";

import { useEffect, useRef } from "react";
import Link from "next/link";
import { Download, Smartphone, X } from "lucide-react";

/** دیپ‌لینک به اپلیکیشن اندروید با بازگشت به بخش دانلود در صورت نبود برنامه. */
const APP_INTENT_URL =
  "intent://#Intent;package=ir.daftar.goftogu;S.browser_fallback_url=%2F%23download;end";

export function PurchaseDialog({ open, onClose }: { open: boolean; onClose: () => void }) {
  const ref = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    const dialog = ref.current;
    if (!dialog) return;
    if (open && !dialog.open) dialog.showModal();
    if (!open && dialog.open) dialog.close();
  }, [open]);

  return (
    <dialog
      ref={ref}
      onClose={onClose}
      onClick={(event) => {
        if (event.target === ref.current) onClose();
      }}
      className="w-[min(92vw,460px)] rounded-[28px] border border-white/70 bg-white p-0 text-ink shadow-lift"
    >
      <div className="p-6 sm:p-7">
        <div className="flex items-start justify-between gap-4">
          <span className="inline-flex h-11 w-11 items-center justify-center rounded-2xl bg-gradient-to-br from-brand-400 via-brand-500 to-brand-700 text-white shadow-glow">
            <Smartphone className="h-5 w-5" />
          </span>
          <button
            type="button"
            onClick={onClose}
            aria-label="بستن"
            className="inline-flex h-9 w-9 items-center justify-center rounded-xl border border-slate-200 bg-white text-ink-muted transition hover:border-brand-200 hover:text-brand-700"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        <h2 className="mt-5 text-[19px] font-bold tracking-tight text-ink">مکالمات من پلاسِ شما</h2>
        <p className="mt-3 text-[13.5px] leading-8 text-ink-soft">
          خرید اشتراک داخل برنامه انجام می‌شود. پس از ورود، واریز و ثبت رسید، مدیر پرداخت را بررسی می‌کند و
          اشتراک فعال می‌شود.
        </p>

        <a href={APP_INTENT_URL} className="btn-primary mt-6 w-full">
          <Smartphone className="h-4 w-4" />
          ادامه در برنامه
        </a>

        <Link
          href="/#download"
          onClick={onClose}
          className="mt-3 inline-flex w-full items-center justify-center gap-2 text-[12.5px] font-medium text-ink-muted transition hover:text-brand-600"
        >
          <Download className="h-3.5 w-3.5" />
          هنوز برنامه را ندارم؛ دریافت نسخهٔ اندروید
        </Link>
      </div>
    </dialog>
  );
}
