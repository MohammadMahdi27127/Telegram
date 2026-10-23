"use client";

import Link from "next/link";
import { ArrowLeft, Sparkles, Star } from "lucide-react";
import { Reveal, Sparkle } from "@/components/reveal";

function Star3D({ className = "h-32 w-32" }: { className?: string }) {
  return (
    <span className={`relative inline-block ${className}`}>
      <svg viewBox="0 0 120 120" className="absolute inset-0 translate-x-1 translate-y-2 opacity-60 blur-[2px]">
        <defs>
          <linearGradient id="starShadow" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stopColor="#4c1d95" />
            <stop offset="100%" stopColor="#2e1065" />
          </linearGradient>
        </defs>
        <path
          d="M60 6l14.7 34.3 37.3 3.4-28.2 24.6 8.4 36.5L60 85.9 28.8 104.8l8.4-36.5L9 43.7l37.3-3.4L60 6z"
          fill="url(#starShadow)"
        />
      </svg>
      <svg viewBox="0 0 120 120" className="relative animate-floaty">
        <defs>
          <linearGradient id="starFace" x1="0.1" y1="0" x2="0.9" y2="1">
            <stop offset="0%" stopColor="#c4b5fd" />
            <stop offset="45%" stopColor="#8b5cf6" />
            <stop offset="100%" stopColor="#6d28d9" />
          </linearGradient>
        </defs>
        <path
          d="M60 6l14.7 34.3 37.3 3.4-28.2 24.6 8.4 36.5L60 85.9 28.8 104.8l8.4-36.5L9 43.7l37.3-3.4L60 6z"
          fill="url(#starFace)"
        />
        <path
          d="M60 16l10.4 24.3 26.4 2.4-20 17.4 6 25.9L60 73.6 37.2 86l6-25.9-20-17.4 26.4-2.4L60 16z"
          fill="#ffffff"
          opacity="0.28"
        />
      </svg>
    </span>
  );
}

export function PlusShowcase() {
  return (
    <section className="py-10 sm:py-16">
      <div className="section-shell">
        <Reveal>
          <div className="relative overflow-hidden rounded-[36px] border border-plus-100 bg-gradient-to-bl from-plus-50 via-white to-brand-50/70 p-6 shadow-soft sm:p-10 lg:p-12">
            <div className="pointer-events-none absolute -left-16 -top-20 h-64 w-64 rounded-full bg-plus-300/30 blur-3xl" />
            <div className="pointer-events-none absolute -bottom-24 right-0 h-64 w-64 rounded-full bg-brand-200/40 blur-3xl" />

            <div className="relative grid items-center gap-10 lg:grid-cols-[1.1fr_0.9fr]">
              <div>
                <span className="inline-flex items-center gap-2 rounded-full border border-plus-100 bg-white/80 px-4 py-1.5 text-xs font-medium text-plus-700 backdrop-blur">
                  <Sparkles className="h-3.5 w-3.5" />
                  یک ستاره، برای کارهای حرفه‌ای شما
                </span>

                <h2 className="heading-lg mt-5 text-ink">
                  گزارش‌های شما،
                  <br />
                  <span className="text-plus-600">کنار هم، در یک تیم.</span>
                </h2>

                <p className="mt-4 max-w-xl text-[14.5px] leading-8 text-ink-soft">
                  دانلود اکسل و متن برای همه رایگان است. با مکالمات من پلاس تیم بسازید، همکارانتان را دعوت کنید و
                  گزارش‌های انتخابی را به اشتراک بگذارید.
                </p>

                <ul className="mt-6 flex flex-wrap gap-2.5">
                  {["ساخت تیم و دعوت اعضا", "پروندهٔ مشترک", "نشان ستاره کنار نام"].map((item) => (
                    <li key={item} className="chip border-plus-100 bg-white/70 text-plus-700">
                      <Sparkle className="h-3 w-3" />
                      {item}
                    </li>
                  ))}
                </ul>

                <Link href="/pricing" className="btn-primary mt-8 !bg-plus-600 !shadow-plus hover:!bg-plus-700">
                  دیدن پلن‌ها و قیمت‌ها
                  <ArrowLeft className="h-4 w-4" />
                </Link>
              </div>

              <div className="flex flex-col items-center gap-7">
                <Star3D className="h-36 w-36 sm:h-44 sm:w-44" />

                <div className="w-full max-w-xs rounded-3xl border border-white/80 bg-white/85 p-3.5 shadow-lift backdrop-blur-xl">
                  <div className="flex items-center gap-3">
                    <span className="inline-flex h-11 w-11 items-center justify-center rounded-2xl bg-gradient-to-br from-plus-500 to-plus-700 text-[15px] font-bold text-white">
                      م
                    </span>
                    <span className="flex flex-col gap-1 leading-tight">
                      <span className="flex items-center gap-1.5 text-[14px] font-bold text-ink">
                        محمد مهدی
                        <Star className="h-3.5 w-3.5 fill-plus-500 text-plus-500" />
                      </span>
                      <span className="text-[11px] font-medium text-plus-600">مکالمات من ویژه</span>
                    </span>
                    <span className="ms-auto rounded-full bg-plus-50 px-2.5 py-1 text-[10px] font-medium text-plus-700">
                      نمونهٔ نشان
                    </span>
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
