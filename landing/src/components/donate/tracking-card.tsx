"use client";

import { Smartphone, Download, ExternalLink } from "lucide-react";

export function TrackingCard() {
  const openApp = () => {
    // آدرس دیپ‌لینک اپلیکیشن
    const deepLink = "recamender://open";
    // آدرس فال‌بک برای دانلود APK
    const fallbackUrl = "/downloads/Tel.apk";

    // تلاش برای باز کردن اپلیکیشن
    window.location.href = deepLink;

    // اگر اپلیکیشن نصب نباشد، بعد از ۲ ثانیه به صفحه دانلود هدایت می‌شود
    const timeout = setTimeout(() => {
      if (confirm("اپلیکیشن هم‌صحبت نصب نیست یا باز نشد. آیا می‌خواهید فایل نصبی را دانلود کنید؟")) {
        window.location.href = fallbackUrl;
      }
    }, 2500);

    // جلوگیری از اجرای تایمر در صورت خروج از صفحه (مثلاً باز شدن اپ)
    window.onblur = () => clearTimeout(timeout);
  };

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
        برای مدیریت گفتگوها، ثبت شماره پیگیری و استفاده از تمامی امکانات، می‌توانید مستقیماً وارد اپلیکیشن
        «هم‌صحبت» شوید.
      </p>

      <div className="mt-8 space-y-4">
        <button
          onClick={openApp}
          className="inline-flex h-14 w-full items-center justify-center gap-3 rounded-2xl bg-brand-500 text-[15px] font-bold text-white shadow-glow transition hover:-translate-y-0.5 hover:bg-brand-600"
        >
          <ExternalLink className="h-5 w-5" />
          باز کردن اپلیکیشن هم‌صحبت
        </button>

        <div className="flex items-center gap-3">
          <div className="h-px flex-1 bg-slate-200" />
          <span className="text-[11px] font-medium text-ink-faint">یا</span>
          <div className="h-px flex-1 bg-slate-200" />
        </div>

        <a
          href="/downloads/Tel.apk"
          className="inline-flex h-12 w-full items-center justify-center gap-2 rounded-2xl border border-slate-200 bg-white text-[13.5px] font-semibold text-ink-soft transition hover:bg-slate-50"
        >
          <Download className="h-4 w-4" />
          دانلود مستقیم فایل APK
        </a>
      </div>

      <p className="mt-6 rounded-2xl border border-amber-200 bg-amber-50/70 p-4 text-[12px] leading-6 text-amber-800">
        نکته: در صورتی که دکمه بالا اپلیکیشن را باز نکرد، احتمالاً نسخهٔ قدیمی دارید. پیشنهاد می‌شود آخرین
        نسخه را دانلود و نصب کنید.
      </p>
    </section>
  );
}
