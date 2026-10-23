"use client";

import { motion } from "framer-motion";
import { CheckCheck, Mic, Paperclip, Send, Sparkles } from "lucide-react";

function Bubble({
  channel,
  body,
  time,
  note = false,
  outgoing = false,
  delay = 0,
}: {
  channel: string;
  body: string;
  time: string;
  note?: boolean;
  outgoing?: boolean;
  delay?: number;
}) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 14, scale: 0.98 }}
      whileInView={{ opacity: 1, y: 0, scale: 1 }}
      viewport={{ once: true, amount: 0.4 }}
      transition={{ duration: 0.5, delay, ease: [0.22, 1, 0.36, 1] }}
      className={`max-w-[85%] ${outgoing ? "self-start" : "self-end"}`}
    >
      <div
        className={`relative rounded-2xl px-3.5 py-2.5 text-[12.5px] leading-6 shadow-sm ${
          note
            ? "border-2 border-dashed border-[#d4a373] bg-[#fff7d6] text-[#7c5c14]"
            : "bg-brand-500 text-white"
        }`}
      >
        <span
          className={`mb-1 block text-[10px] font-bold ${
            note ? "text-[#8d6e00]" : "text-white/80"
          }`}
        >
          {channel}
        </span>
        <p className={note ? "text-[#5f4a10]" : "text-white"}>{body}</p>
        <span
          className={`mt-1.5 flex items-center justify-end gap-1 text-[10px] ${
            note ? "text-[#a8892f]" : "text-white/75"
          }`}
        >
          {time}
          {!note && <CheckCheck className="h-3.5 w-3.5" />}
        </span>
      </div>
    </motion.div>
  );
}

function Composer({ placeholder = "نوشتن گزاره…" }: { placeholder?: string }) {
  return (
    <div className="mt-3 flex items-center gap-2 rounded-full border border-slate-200 bg-white px-3 py-2 shadow-sm">
      <span className="flex-1 text-[12px] text-ink-faint">{placeholder}</span>
      <Paperclip className="h-4 w-4 text-ink-faint" />
      <span className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-brand-500 text-white shadow-glow">
        <Mic className="h-4 w-4" />
      </span>
    </div>
  );
}

export function AppPreview() {
  return (
    <div className="relative">
      <div className="pointer-events-none absolute -inset-6 -z-10 rounded-[42px] bg-gradient-to-br from-brand-200/50 via-white/0 to-plus-100/60 blur-2xl" />
      <span className="mb-3 inline-flex items-center gap-2 rounded-full border border-white/70 bg-white/80 px-3.5 py-1.5 text-[11.5px] font-medium text-ink-muted shadow-soft backdrop-blur">
        <Sparkles className="h-3.5 w-3.5 text-brand-500" />
        گفتگوهایتان، همیشه همراهتان
      </span>

      <div className="animate-floaty rounded-[30px] border border-white/80 bg-white/85 p-3 shadow-lift backdrop-blur-xl">
        <div className="rounded-[24px] bg-canvas/70 p-3.5">
          <div className="flex items-center gap-3 rounded-2xl border border-white/70 bg-white px-3 py-2.5 shadow-sm">
            <span className="inline-flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-brand-400 to-brand-600 text-[13px] font-bold text-white">
              م
            </span>
            <span className="flex flex-col leading-tight">
              <span className="text-[13px] font-bold text-ink">دفتر من</span>
              <span className="text-[10.5px] text-ink-muted">یک جای امن برای فکرها</span>
            </span>
            <span className="ms-auto h-2 w-2 rounded-full bg-emerald-400" />
          </div>

          <div className="mt-3 flex flex-col gap-2.5">
            <Bubble
              outgoing
              channel="گفتگوی حضوری"
              body="ایده‌های جلسه، تصمیم‌ها و قدم بعدی را یک‌جا ثبت می‌کنیم."
              time="۱۰:۳۰"
              delay={0.05}
            />
            <Bubble
              note
              channel="یادداشت من"
              body="یادمان باشد نسخهٔ نهایی طرح را با تیم به اشتراک بگذاریم."
              time="۱۰:۳۲"
              delay={0.15}
            />
          </div>

          <Composer />
        </div>
      </div>
    </div>
  );
}

export function ThreadPreview() {
  return (
    <div className="relative">
      <div className="pointer-events-none absolute -inset-8 -z-10 rounded-[46px] bg-gradient-to-tr from-brand-200/40 via-white/0 to-plus-100/50 blur-3xl" />

      <div className="rounded-[32px] border border-white/80 bg-white/85 p-4 shadow-lift backdrop-blur-xl sm:p-5">
        <div className="flex items-center justify-between gap-3">
          <span className="inline-flex items-center gap-2 rounded-full border border-brand-100 bg-brand-50/80 px-3.5 py-1.5 text-[11.5px] font-medium text-brand-700">
            <Sparkles className="h-3.5 w-3.5" />
            دفتر گفتگوها
          </span>
          <span className="text-[11px] text-ink-faint">نمونهٔ نمایشی</span>
        </div>

        <div className="mt-4 rounded-3xl bg-canvas/70 p-4">
          <div className="flex items-start gap-3 rounded-2xl border border-white/70 bg-white p-3.5 shadow-sm">
            <span className="inline-flex h-11 w-11 items-center justify-center rounded-2xl bg-gradient-to-br from-plus-500 to-plus-700 text-[15px] font-bold text-white shadow-plus">
              ر
            </span>
            <span className="flex flex-col gap-0.5 leading-tight">
              <span className="text-[14px] font-bold text-ink">گفتگوی جلسهٔ امروز</span>
              <span className="text-[11.5px] text-ink-muted">پروندهٔ تیم طراحی · نمونهٔ نمایشی</span>
            </span>
            <span className="ms-auto text-[10.5px] text-ink-faint">۱۰:۳۰</span>
          </div>

          <div className="mt-3 flex flex-col gap-2.5">
            <Bubble
              outgoing
              channel="گفتگوی حضوری"
              body="ایده‌های جلسه، تصمیم‌ها و قدم بعدی را یک‌جا ثبت می‌کنیم."
              time="۱۰:۳۰"
              delay={0.05}
            />
            <Bubble
              note
              channel="یادداشت من"
              body="یادمان باشد نسخهٔ نهایی طرح را با تیم به اشتراک بگذاریم."
              time="۱۰:۳۲"
              delay={0.12}
            />
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, amount: 0.4 }}
              transition={{ duration: 0.5, delay: 0.2 }}
              className="self-start rounded-2xl bg-white px-3.5 py-2 text-[12px] text-ink-muted shadow-sm"
            >
              اشتراک‌گذاری با تیم طراحی انجام شد
            </motion.div>
          </div>

          <div className="mt-3 flex items-center gap-2 rounded-full border border-slate-200 bg-white px-3 py-2 shadow-sm">
            <span className="flex-1 text-[12px] text-ink-faint">نوشتن گزاره…</span>
            <Paperclip className="h-4 w-4 text-ink-faint" />
            <span className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-brand-500 text-white shadow-glow">
              <Send className="h-4 w-4" />
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
