"use client";

import Link from "next/link";
import { AlertTriangle, ArrowLeft, Download, Smartphone } from "lucide-react";
import { Reveal } from "@/components/reveal";
import { SITE } from "@/lib/site";

export function DownloadCta() {
  return (
    <section id="download" className="scroll-mt-24 py-10 sm:py-16">
      <div className="section-shell">
        <Reveal>
          <div className="relative overflow-hidden rounded-[36px] bg-gradient-to-bl from-[#0b2b46] via-[#12507a] to-brand-600 p-7 text-white shadow-lift sm:p-11 lg:p-14">
            <div className="pointer-events-none absolute -right-24 -top-24 h-72 w-72 rounded-full bg-brand-400/25 blur-3xl" />
            <div className="pointer-events-none absolute -bottom-28 -left-16 h-72 w-72 rounded-full bg-plus-500/20 blur-3xl" />

            <div className="relative grid items-center gap-10 lg:grid-cols-[1.15fr_0.85fr]">
              <div>
                <span className="inline-flex items-center gap-2 rounded-full border border-white/25 bg-white/10 px-4 py-1.5 text-xs font-medium text-white/90 backdrop-blur">
                  <Smartphone className="h-3.5 w-3.5" />
                  یک گفتگوی تازه، یک شروع تازه
                </span>

                <h2 className="mt-5 text-2xl font-bold leading-[1.45] sm:text-3xl lg:text-[38px]">
                  دفتر بعدی شما،
                  <br />
                  <span className="text-brand-200">همین‌جاست.</span>
                </h2>

                <p className="mt-4 max-w-xl text-[14.5px] leading-8 text-white/80">
                  رایگان شروع کنید؛ گزارش‌ها و پرونده‌هایتان را به صورت اکسل و متن دریافت کنید.
                </p>

                <div className="mt-8 flex flex-col items-stretch gap-3 sm:flex-row sm:items-center">
                  <a
                    href={SITE.androidApk}
                    download
                    className="btn bg-white text-brand-700 shadow-lift hover:-translate-y-0.5 hover:bg-brand-50"
                  >
                    <Download className="h-4 w-4" />
                    دانلود نسخهٔ اندروید
                  </a>
                  <span className="text-[12.5px] text-white/70">اندروید ۸ و بالاتر · ذخیرهٔ آفلاین روی گوشی</span>
                </div>
              </div>

              <div className="rounded-3xl border border-white/20 bg-white/10 p-5 backdrop-blur-xl">
                <div className="flex items-start gap-3">
                  <span className="inline-flex h-10 w-10 shrink-0 items-center justify-center rounded-2xl bg-amber-400/20 text-amber-200">
                    <AlertTriangle className="h-5 w-5" />
                  </span>
                  <div>
                    <h3 className="text-[14px] font-bold text-white">نسخهٔ آزمایشی قبلی را دارید؟</h3>
                    <p className="mt-2 text-[12.5px] leading-7 text-white/75">
                      پیش از نصب نسخهٔ جدید، داده‌ها را همگام و پشتیبان‌گیری کنید.
                    </p>
                    <Link
                      href={SITE.previewApk}
                      className="mt-4 inline-flex items-center gap-1.5 text-[12.5px] font-semibold text-brand-200 transition hover:text-white"
                    >
                      به‌روزرسانی نسخهٔ آزمایشی قبلی
                      <ArrowLeft className="h-3.5 w-3.5" />
                    </Link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </Reveal>
      </div>
    </section>
  );
}
