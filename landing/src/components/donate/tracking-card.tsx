"use client";

import { useCallback, useRef } from "react";
import { Download, ExternalLink, Smartphone } from "lucide-react";
import { SITE } from "@/lib/site";

/** مدت انتظار برای تشخیص باز شدن اپلیکیشن (میلی‌ثانیه). */
const DEEP_LINK_TIMEOUT_MS = 1500;

export function TrackingCard() {
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const openApp = useCallback(() => {
    // اگر اپلیکیشن باز شد، صفحه در پس‌زمینه قرار می‌گیرد و تایمر کنسل می‌شود.
    const onHidden = () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
    window.addEventListener("blur", onHidden, { once: true });
    document.addEventListener("visibilitychange", onHidden, { once: true });

    // تلاش برای باز کردن اپلیکیشن با اسکیم اختصاصی
    window.location.href = SITE.androidDeepLink;

    // در صورت نبود اپلیکیشن، پیشنهاد دانلود مستقیم فایل نصبی
    timerRef.current = setTimeout(() => {
      window.removeEventListener("blur", onHidden);
      document.removeEventListener("visibilitychange", onHidden);
      const wantsDownload = window.confirm(
        "اپلیکیشن روی این دستگاه باز نشد. آیا فایل نصبی (APK) دانلود شود؟",
      );
      if (wantsDownload) window.location.href = SITE.androidApk;
    }, DEEP_LINK_TIMEOUT_MS);
  }, []);

  return (
    <section className="card flex flex-col p-6 sm:p-7">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-[12px] font-medium text-brand-500">دسترسی سریع</p>
          <h2 className="mt-2 text-[20px] font-bold tracking-tight text-ink">ورود به اپلیکیشن</h2>
        </div>
        <span className="inline-flex h-9 w-9 items-center justify-center rounded-full border border-brand-200 bg-brand-50 text-brand-600">
          <Smartphone className="h-4 w-4" />
        </span>
      </div>

      <p className="mt-5 text-[13px] leading-7 text-ink-muted">
        برای مدیریت گفتگوها، ثبت شماره پیگیری و استفاده از تمامی امکانات می‌توانید مستقیم وارد اپلیکیشن
        «هم‌صحبت» شوید.
      </p>

      <div className="mt-8 space-y-4">
        <button
          type="button"
          onClick={openApp}
          className="inline-flex h-14 w-full items-center justify-center gap-3 rounded-2xl bg-brand-500 text-[15px] font-bold text-white shadow-glow transition hover:-translate-y-0.5 hover:bg-brand-600"
        >
          <ExternalLink className="h-5 w-5" />
          بازگشت به اپلیکیشن گفت‌وگو
        </button>

        <div className="flex items-center gap-3">
          <div className="h-px flex-1 bg-slate-200" />
          <span className="text-[11px] font-medium text-ink-faint">یا</span>
          <div className="h-px flex-1 bg-slate-200" />
        </div>

        <a
          href={SITE.androidApk}
          className="inline-flex h-12 w-full items-center justify-center gap-2 rounded-2xl border border-slate-200 bg-white text-[13.5px] font-semibold text-ink-soft transition hover:bg-slate-50"
        >
          <Download className="h-4 w-4" />
          دانلود مستقیم فایل APK
        </a>
      </div>

      <p className="mt-6 rounded-2xl border border-amber-200 bg-amber-50/70 p-4 text-[12px] leading-6 text-amber-800">
        نکته: اگر دکمهٔ بالا اپلیکیشن را باز نکرد، احتمالاً نسخهٔ قدیمی نصب دارید؛ آخرین نسخه را دانلود و نصب
        کنید.
      </p>
    </section>
  );
}
