"use client";

import { Info, Landmark, ShieldCheck, Wallet } from "lucide-react";
import { CopyRow } from "@/components/donate/copy-row";

/** اطلاعات واقعی مقصد واریز که از تنظیمات سامانه دریافت می‌شود. */
const CARD_NUMBER_VALUE = "6219861909086315";
const CARD_NUMBER_DISPLAY = "۶۲۱۹-۸۶۱۹-۰۹۰۸-۶۳۱۵";
const IBAN_VALUE = "IR540560611828005125842101";
const IBAN_DISPLAY = "IR54 0560 6118 2800 5125 8421 01";
const ACCOUNT_OWNER = "محمدجواد رضوانیان";

export function BankCard() {
  return (
    <section className="card flex flex-col p-6 sm:p-7">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-[12px] font-medium text-rose-500">همراهی شما ارزشمند است</p>
          <h2 className="mt-2 text-[20px] font-bold tracking-tight text-ink">مقصد حمایت مالی</h2>
        </div>
        <span className="inline-flex items-center gap-1.5 rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-[11px] font-medium text-amber-700">
          <Wallet className="h-3.5 w-3.5" />
          واریز دلخواه
        </span>
      </div>

      {/* کارت بانکی */}
      <div className="relative mt-6 overflow-hidden rounded-3xl bg-gradient-to-br from-slate-900 via-slate-800 to-brand-900 p-6 text-white shadow-lift">
        <span
          className="pointer-events-none absolute -left-10 -top-16 h-44 w-44 rounded-full bg-rose-400/25 blur-3xl"
          aria-hidden="true"
        />
        <span
          className="pointer-events-none absolute -bottom-20 -right-12 h-48 w-48 rounded-full bg-brand-400/25 blur-3xl"
          aria-hidden="true"
        />

        <div className="relative flex items-start justify-between gap-4">
          <div>
            <p className="text-[13px] font-bold text-white">مکالمات من</p>
            <p className="mt-1 text-[11.5px] text-white/60">برای حرف‌هایی که می‌مانند</p>
          </div>
          <span className="inline-flex h-9 w-11 items-center justify-center rounded-lg bg-gradient-to-br from-amber-200 to-amber-400 shadow-inner">
            <Landmark className="h-4 w-4 text-amber-900/80" />
          </span>
        </div>

        <p className="relative mt-7 text-[11.5px] text-white/55">شماره کارت</p>
        <CopyRow
          value={CARD_NUMBER_VALUE}
          display={CARD_NUMBER_DISPLAY}
          className="mt-2"
          displayClassName="text-[17px] font-bold tracking-[0.06em] text-white sm:text-[19px]"
        />

        <p className="relative mt-5 text-[12.5px] text-white/80">
          به نام: <span className="font-semibold text-white">{ACCOUNT_OWNER}</span>
        </p>
      </div>

      {/* شماره شبا */}
      <div className="mt-5 rounded-3xl border border-slate-200/80 bg-slate-50/80 p-4">
        <p className="text-[11.5px] font-medium text-ink-muted">شماره شبا</p>
        <CopyRow
          value={IBAN_VALUE}
          display={IBAN_DISPLAY}
          tone="light"
          className="mt-2"
          displayClassName="text-[13.5px] font-semibold text-ink"
        />
      </div>

      <div className="mt-5 space-y-3">
        <p className="flex items-start gap-2 text-[12.5px] leading-6 text-ink-muted">
          <Info className="mt-0.5 h-4 w-4 shrink-0 text-brand-500" />
          مقصد واریز از تنظیمات به‌روز سامانه دریافت می‌شود.
        </p>
        <p className="flex items-start gap-2 rounded-2xl border border-rose-100 bg-rose-50/70 p-3.5 text-[12.5px] leading-6 text-rose-700">
          <ShieldCheck className="mt-0.5 h-4 w-4 shrink-0" />
          پیش از واریز، نام صاحب حساب را در برنامهٔ بانکی خود بررسی کنید. مکالمات من اطلاعات کارت بانکی شما را
          در این صفحه دریافت نمی‌کند.
        </p>
      </div>
    </section>
  );
}
