"use client";

import Link from "next/link";
import { motion, useReducedMotion } from "framer-motion";
import { ArrowLeft, Check, Download, Sparkles } from "lucide-react";
import { AppPreview } from "@/components/chat-mockup";

const VALUE_BADGES = ["شروع رایگان", "دفتر شخصی خصوصی", "در دسترس، حتی آفلاین"];

export function Hero() {
  const reduce = useReducedMotion();
  const rise = (delay: number) =>
    reduce
      ? {}
      : {
          initial: { opacity: 0, y: 18 },
          animate: { opacity: 1, y: 0 },
          transition: { duration: 0.6, delay, ease: [0.22, 1, 0.36, 1] as const },
        };

  return (
    <section className="relative overflow-hidden pb-6 pt-10 sm:pt-14 lg:pb-16 lg:pt-20">
      <div className="pointer-events-none absolute inset-x-0 top-0 -z-10 h-[520px] bg-gradient-to-b from-white/70 to-transparent" />

      <div className="section-shell grid items-center gap-12 lg:grid-cols-[1.05fr_0.95fr] lg:gap-14">
        <div className="text-center lg:text-right">
          <motion.span
            {...rise(0)}
            className="inline-flex items-center gap-2 rounded-full border border-brand-100 bg-brand-50/90 px-4 py-2 text-[12.5px] font-medium text-brand-700 shadow-soft backdrop-blur"
          >
            <Sparkles className="h-3.5 w-3.5" />
            هر گفتگویی، ارزش به‌خاطر ماندن دارد.
          </motion.span>

          <motion.h1 {...rise(0.06)} className="heading-xl mt-6 text-ink">
            گفتگوها می‌گذرند.
            <br />
            <span className="bg-gradient-to-l from-brand-500 via-brand-600 to-plus-600 bg-clip-text text-transparent">
              مکالمات من می‌ماند.
            </span>
          </motion.h1>

          <motion.p {...rise(0.12)} className="mx-auto mt-5 max-w-xl text-[15px] leading-8 text-ink-soft lg:mx-0">
            گفتگوها، یادداشت‌ها و تصمیم‌های مهمتان را یک‌جا نگه دارید؛ از دفتر شخصی تا پرونده‌های مشترک تیم.
          </motion.p>

          <motion.div
            {...rise(0.18)}
            className="mt-8 flex flex-col items-stretch gap-3 sm:flex-row sm:items-center sm:justify-center lg:justify-start"
          >
            <Link href="/#download" className="btn-primary">
              <Download className="h-4 w-4" />
              مکالمات من را دریافت کنید
            </Link>
            <Link href="/#features" className="btn-outline">
              آشنایی با امکانات
              <ArrowLeft className="h-4 w-4" />
            </Link>
          </motion.div>

          <motion.ul {...rise(0.24)} className="mt-7 flex flex-wrap items-center justify-center gap-2.5 lg:justify-start">
            {VALUE_BADGES.map((badge) => (
              <li key={badge} className="chip">
                <Check className="h-3.5 w-3.5 text-brand-500" />
                {badge}
              </li>
            ))}
          </motion.ul>
        </div>

        <motion.div
          {...(reduce
            ? {}
            : {
                initial: { opacity: 0, x: -26 },
                animate: { opacity: 1, x: 0 },
                transition: { duration: 0.7, delay: 0.1, ease: [0.22, 1, 0.36, 1] as const },
              })}
          className="mx-auto w-full max-w-md lg:max-w-none"
        >
          <AppPreview />
        </motion.div>
      </div>
    </section>
  );
}
