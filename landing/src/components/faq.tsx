"use client";

import { useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { Plus } from "lucide-react";
import { Reveal } from "@/components/reveal";
import { FAQS } from "@/lib/site";

export function Faq() {
  const [open, setOpen] = useState<number | null>(0);

  return (
    <section id="questions" className="scroll-mt-24 py-10 sm:py-16">
      <div className="section-shell">
        <Reveal className="text-center">
          <span className="kicker">پاسخ‌های روشن</span>
          <h2 className="heading-lg mt-5 text-ink">
            قبل از شروع،
            <br />
            <span className="text-brand-600">شاید بپرسید.</span>
          </h2>
          <p className="mx-auto mt-4 max-w-xl text-[14.5px] leading-7 text-ink-muted">
            مکالمات من برای ساده‌تر شدن کار شما ساخته شده.
          </p>
        </Reveal>

        <div className="mx-auto mt-10 flex max-w-3xl flex-col gap-3">
          {FAQS.map((item, index) => {
            const isOpen = open === index;
            return (
              <Reveal key={item.q} delay={index * 0.05}>
                <div
                  className={`overflow-hidden rounded-3xl border bg-white/85 backdrop-blur transition-all duration-300 ${
                    isOpen ? "border-brand-200 shadow-lift" : "border-slate-200/70 shadow-soft"
                  }`}
                >
                  <button
                    type="button"
                    onClick={() => setOpen(isOpen ? null : index)}
                    aria-expanded={isOpen}
                    className="flex w-full items-center gap-4 px-5 py-[18px] text-right sm:px-6 sm:py-5"
                  >
                    <span
                      className={`inline-flex h-9 w-9 shrink-0 items-center justify-center rounded-xl transition-all duration-300 ${
                        isOpen ? "rotate-45 bg-brand-500 text-white" : "bg-brand-50 text-brand-600"
                      }`}
                    >
                      <Plus className="h-4 w-4" />
                    </span>
                    <span className={`flex-1 text-[14.5px] font-semibold leading-7 ${isOpen ? "text-brand-700" : "text-ink"}`}>
                      {item.q}
                    </span>
                  </button>

                  <AnimatePresence initial={false}>
                    {isOpen && (
                      <motion.div
                        key="content"
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: "auto", opacity: 1 }}
                        exit={{ height: 0, opacity: 0 }}
                        transition={{ duration: 0.3, ease: [0.22, 1, 0.36, 1] }}
                        className="overflow-hidden"
                      >
                        <p className="px-5 pb-5 text-[13.5px] leading-8 text-ink-muted sm:px-6 sm:pb-6 sm:ps-[68px]">
                          {item.a}
                        </p>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </div>
              </Reveal>
            );
          })}
        </div>
      </div>
    </section>
  );
}
