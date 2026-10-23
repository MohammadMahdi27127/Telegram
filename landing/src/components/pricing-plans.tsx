"use client";

import Link from "next/link";
import { useState } from "react";
import { motion } from "framer-motion";
import { ArrowLeft, Check } from "lucide-react";
import { Reveal } from "@/components/reveal";
import { PurchaseDialog } from "@/components/purchase-dialog";
import { PLANS } from "@/lib/site";
import { toFa } from "@/lib/fa";

type Cycle = "monthly" | "seasonal";

const MONTHLY_PRICE: Record<string, number> = { plus: 1_000_000, smart: 6_000_000 };
const FULL_PRICE: Record<string, number> = { plus: 3_000_000, smart: 18_000_000 };
/** تخفیف دورهٔ فصلی: پنج‌ششم مبلغ سه ماه (حدود ۱۷٪). */
const SEASONAL_FACTOR = 5 / 6;
const SEASONAL_MONTHS = 3;

function PurpleStar({ className = "h-3.5 w-3.5" }: { className?: string }) {
  return (
    <svg viewBox="0 0 24 24" className={className} aria-hidden="true">
      <defs>
        <linearGradient id="plusStarFace" x1="0.1" y1="0" x2="0.9" y2="1">
          <stop offset="0%" stopColor="#c4b5fd" />
          <stop offset="45%" stopColor="#8b5cf6" />
          <stop offset="100%" stopColor="#6d28d9" />
        </linearGradient>
      </defs>
      <path
        fill="url(#plusStarFace)"
        d="M12 2.4l2.9 6.2 6.8.7-5.1 4.5 1.5 6.6L12 17.2l-6.1 3.2 1.5-6.6-5.1-4.5 6.8-.7L12 2.4z"
      />
      <path
        fill="#ffffff"
        opacity="0.35"
        d="M12 6.5l1.5 3.2 3.5.4-2.6 2.3.8 3.4-3.2-1.7-3.2 1.7.8-3.4L7 10.1l3.5-.4L12 6.5z"
      />
    </svg>
  );
}

export function PricingPlans() {
  const [cycle, setCycle] = useState<Cycle>("monthly");
  const [purchaseOpen, setPurchaseOpen] = useState(false);

  const priceOf = (id: string) => {
    if (id === "free") return null;
    const monthly = MONTHLY_PRICE[id];
    if (cycle === "monthly") {
      return { old: `${toFa(FULL_PRICE[id])} تومان`, price: toFa(monthly), period: "تومان / ماهانه" };
    }
    const full = monthly * SEASONAL_MONTHS;
    return {
      old: `${toFa(full)} تومان`,
      price: toFa(Math.round(full * SEASONAL_FACTOR)),
      period: "تومان / فصلی",
    };
  };

  return (
    <div className="section-shell">
      <Reveal className="text-center">
        <span className="kicker">به اندازهٔ نیازتان، مکالمات من شوید</span>
        <h1 className="heading-xl mt-5 text-ink">
          شروع رایگان.
          <br />
          <span className="text-brand-600">یک قدم جلوتر، با پلاس.</span>
        </h1>
        <p className="mx-auto mt-5 max-w-2xl text-[14.5px] leading-8 text-ink-soft">
          دفتر، خروجی اکسل و متن و خلاصه‌سازی هوش مصنوعی رایگان است؛ اشتراک‌گذاری گزارش‌ها و کار تیمی با پلن پلاس
          در دسترس است.
        </p>
      </Reveal>

      <Reveal delay={0.08} className="mt-9 flex flex-col items-center gap-3">
        <div className="inline-flex items-center gap-1 rounded-2xl border border-slate-200 bg-white/80 p-1.5 shadow-soft backdrop-blur">
          {(
            [
              { id: "monthly", label: "ماهانه (۳۰ روز)" },
              { id: "seasonal", label: "فصلی (۹۰ روز)" },
            ] as const
          ).map((option) => (
            <button
              key={option.id}
              type="button"
              onClick={() => setCycle(option.id)}
              className={`relative rounded-xl px-5 py-2.5 text-[13px] font-semibold transition ${
                cycle === option.id ? "text-white" : "text-ink-soft hover:text-brand-700"
              }`}
            >
              {cycle === option.id && (
                <motion.span
                  layoutId="cycle-pill"
                  className="absolute inset-0 -z-10 rounded-xl bg-brand-500 shadow-glow"
                  transition={{ type: "spring", stiffness: 380, damping: 32 }}
                />
              )}
              {option.label}
              {option.id === "seasonal" && (
                <span
                  className={`ms-2 rounded-full px-2 py-0.5 text-[10px] font-bold ${
                    cycle === "seasonal" ? "bg-white/25 text-white" : "bg-plus-50 text-plus-700"
                  }`}
                >
                  ۱۷٪ تخفیف
                </span>
              )}
            </button>
          ))}
        </div>
        <p className="text-[12px] text-ink-faint">هر دورهٔ ماهانه، ۳۰ روز اعتبار دارد.</p>
      </Reveal>

      <div className="mt-11 grid grid-cols-1 items-stretch gap-6 md:grid-cols-3">
        {PLANS.map((plan, index) => {
          const price = priceOf(plan.id);
          return (
            <Reveal key={plan.id} delay={index * 0.08}>
              <article
                className={`relative flex h-full flex-col rounded-[30px] p-6 transition-all duration-300 sm:p-7 ${
                  plan.highlight
                    ? "border border-brand-200 bg-white shadow-xl ring-2 ring-[#24A1DE] md:scale-[1.02]"
                    : "card hover:-translate-y-1.5 hover:shadow-lift"
                }`}
              >
                <span
                  className={`self-start inline-flex items-center gap-1.5 rounded-full px-3.5 py-1.5 text-[11px] font-medium ${
                    plan.disabled
                      ? "border border-slate-200 bg-slate-50 text-ink-muted"
                      : plan.highlight
                        ? "border border-plus-100 bg-plus-50 text-plus-700"
                        : "border border-slate-200 bg-white text-ink-soft"
                  }`}
                >
                  {plan.highlight && <PurpleStar />}
                  {plan.badge}
                </span>

                <h2 className="mt-4 text-[22px] font-bold text-ink">{plan.name}</h2>
                <p className="mt-2 text-[13px] leading-7 text-ink-muted">{plan.headline}</p>

                <div className="mt-6 rounded-2xl bg-canvas/70 p-4">
                  {price ? (
                    <>
                      <span className="block text-[12px] text-ink-faint line-through">{price.old}</span>
                      <span className="mt-1 flex items-baseline gap-1.5">
                        <span className={`text-[26px] font-bold ${plan.highlight ? "text-brand-600" : "text-ink"}`}>
                          {price.price}
                        </span>
                        <span className="text-[12px] text-ink-muted">{price.period}</span>
                      </span>
                    </>
                  ) : (
                    <>
                      <span className="block text-[12px] text-ink-faint line-through">۰ تومان</span>
                      <span className="mt-1 flex items-baseline gap-1.5">
                        <span className="text-[26px] font-bold text-ink">رایگان</span>
                        <span className="text-[12px] text-ink-muted">{plan.period}</span>
                      </span>
                    </>
                  )}
                </div>

                <ul className="mt-6 flex flex-1 flex-col gap-3">
                  {plan.features.map((feature) => (
                    <li key={feature} className="flex items-start gap-2.5 text-[13px] leading-7 text-ink-soft">
                      <span
                        className={`mt-1.5 inline-flex h-4 w-4 shrink-0 items-center justify-center rounded-full ${
                          plan.highlight ? "bg-brand-100 text-brand-700" : "bg-slate-100 text-ink-muted"
                        }`}
                      >
                        <Check className="h-3 w-3" />
                      </span>
                      {feature}
                    </li>
                  ))}
                </ul>

                {plan.disabled ? (
                  <span className="mt-7 inline-flex w-full cursor-not-allowed items-center justify-center gap-2 rounded-2xl border border-slate-200 bg-slate-50 px-6 py-3.5 text-[14px] font-semibold text-ink-faint">
                    {plan.cta}
                  </span>
                ) : plan.highlight ? (
                  <button
                    type="button"
                    onClick={() => setPurchaseOpen(true)}
                    className="btn-primary mt-7 w-full"
                  >
                    {plan.cta}
                    <ArrowLeft className="h-4 w-4" />
                  </button>
                ) : (
                  <Link href={plan.ctaHref} className="btn-outline mt-7 w-full">
                    {plan.cta}
                    <ArrowLeft className="h-4 w-4" />
                  </Link>
                )}

                <p className="mt-3 text-center text-[11.5px] text-ink-faint">{plan.note}</p>
              </article>
            </Reveal>
          );
        })}
      </div>

      <p className="mt-8 text-center text-[12.5px] text-ink-faint">
        مبالغ به تومان هستند. تمدید به پایان اعتبار فعلی اضافه می‌شود.
      </p>

      <PurchaseDialog open={purchaseOpen} onClose={() => setPurchaseOpen(false)} />
    </div>
  );
}
