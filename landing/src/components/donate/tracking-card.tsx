"use client";

import { useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { BadgeCheck, CheckCircle2, LogOut, Lock, Phone, Receipt } from "lucide-react";

type Account = { name: string; phone: string };
type TrackRecord = { code: string; date: string };

export function TrackingCard() {
  const [account, setAccount] = useState<Account | null>(null);
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [code, setCode] = useState("");
  const [records, setRecords] = useState<TrackRecord[]>([]);

  const login = (event: React.FormEvent) => {
    event.preventDefault();
    if (!phone.trim() || !password.trim()) return;
    setAccount({ name: "کاربر مکالمات من", phone: phone.trim() });
    setPassword("");
  };

  const submitTracking = (event: React.FormEvent) => {
    event.preventDefault();
    const trimmed = code.trim();
    if (!trimmed) return;
    setRecords((prev) => [
      { code: trimmed, date: new Date().toLocaleDateString("fa-IR") },
      ...prev,
    ]);
    setCode("");
  };

  return (
    <section className="card flex flex-col p-6 sm:p-7">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-[12px] font-medium text-rose-500">بعد از واریز، در صورت تمایل</p>
          <h2 className="mt-2 text-[20px] font-bold tracking-tight text-ink">ثبت شماره پیگیری</h2>
        </div>
        <span className="inline-flex h-9 w-9 items-center justify-center rounded-full border border-emerald-200 bg-emerald-50 text-emerald-600">
          <BadgeCheck className="h-4 w-4" />
        </span>
      </div>

      <p className="mt-5 text-[13px] leading-7 text-ink-muted">
        برای ثبت پیگیری، با همان حساب مکالمات من وارد شوید. برای خودِ حمایت مالی نیازی به ورود نیست.
      </p>

      <p className="mt-3 rounded-2xl border border-amber-200 bg-amber-50/70 p-3.5 text-[12.5px] leading-6 text-amber-800">
        ثبت شماره پیگیری به معنی تأیید واریز نیست و تغییری در اشتراک یا امکانات حساب ایجاد نمی‌کند.
      </p>

      <AnimatePresence mode="wait" initial={false}>
        {account ? (
          <motion.div
            key="logged-in"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.22 }}
            className="mt-6 flex flex-1 flex-col"
          >
            <div className="flex items-center justify-between gap-3 rounded-2xl border border-emerald-100 bg-emerald-50/70 px-4 py-3">
              <span className="inline-flex items-center gap-2 text-[12.5px] font-medium text-emerald-700">
                <CheckCircle2 className="h-4 w-4" />
                وارد شده با حساب {account.phone}
              </span>
              <button
                type="button"
                onClick={() => setAccount(null)}
                className="inline-flex items-center gap-1 text-[11.5px] font-medium text-ink-muted transition hover:text-rose-500"
              >
                <LogOut className="h-3.5 w-3.5" />
                خروج
              </button>
            </div>

            <form onSubmit={submitTracking} className="mt-5">
              <label htmlFor="tracking-code" className="text-[12.5px] font-medium text-ink-soft">
                شماره پیگیری واریز / کد ارجاع
              </label>
              <div className="mt-2 flex flex-col gap-2 sm:flex-row">
                <input
                  id="tracking-code"
                  value={code}
                  onChange={(event) => setCode(event.target.value)}
                  placeholder="مثلاً ۱۲۳۴۵۶۷۸"
                  className="h-12 flex-1 rounded-2xl border border-slate-200 bg-white px-4 text-[13.5px] text-ink outline-none transition placeholder:text-ink-faint focus:border-brand-300 focus:ring-4 focus:ring-brand-100"
                />
                <button
                  type="submit"
                  className="inline-flex h-12 items-center justify-center gap-2 rounded-2xl bg-brand-500 px-5 text-[13.5px] font-semibold text-white shadow-glow transition hover:-translate-y-0.5 hover:bg-brand-600"
                >
                  <Receipt className="h-4 w-4" />
                  ثبت شماره پیگیری
                </button>
              </div>
            </form>

            <div className="mt-6">
              <h3 className="text-[13.5px] font-bold text-ink">پیگیری‌های ثبت‌شدهٔ شما</h3>
              {records.length === 0 ? (
                <p className="mt-3 rounded-2xl border border-dashed border-slate-200 bg-slate-50/60 px-4 py-6 text-center text-[12.5px] text-ink-muted">
                  هنوز شماره پیگیری ثبت نکرده‌اید.
                </p>
              ) : (
                <ul className="mt-3 space-y-2">
                  {records.map((record, index) => (
                    <li
                      key={`${record.code}-${index}`}
                      className="flex items-center justify-between gap-3 rounded-2xl border border-slate-200/80 bg-white px-4 py-3"
                    >
                      <span className="text-[13px] font-semibold tabular-nums text-ink" dir="ltr">
                        {record.code}
                      </span>
                      <span className="text-[11.5px] text-ink-faint">{record.date}</span>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </motion.div>
        ) : (
          <motion.div
            key="guest"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.22 }}
            className="mt-6 flex flex-1 flex-col"
          >
            <form onSubmit={login} className="space-y-3">
              <div className="relative">
                <Phone className="pointer-events-none absolute right-4 top-1/2 h-4 w-4 -translate-y-1/2 text-ink-faint" />
                <input
                  type="tel"
                  inputMode="numeric"
                  dir="ltr"
                  value={phone}
                  onChange={(event) => setPhone(event.target.value)}
                  placeholder="0912 123 4567"
                  aria-label="شماره همراه حساب"
                  className="h-12 w-full rounded-2xl border border-slate-200 bg-white pr-11 pl-4 text-[13.5px] text-ink outline-none transition placeholder:text-ink-faint focus:border-brand-300 focus:ring-4 focus:ring-brand-100"
                />
              </div>
              <div className="relative">
                <Lock className="pointer-events-none absolute right-4 top-1/2 h-4 w-4 -translate-y-1/2 text-ink-faint" />
                <input
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="رمز عبور"
                  aria-label="رمز عبور"
                  className="h-12 w-full rounded-2xl border border-slate-200 bg-white pr-11 pl-4 text-[13.5px] text-ink outline-none transition placeholder:text-ink-faint focus:border-brand-300 focus:ring-4 focus:ring-brand-100"
                />
              </div>
              <button
                type="submit"
                className="inline-flex h-12 w-full items-center justify-center rounded-2xl bg-brand-500 text-[13.5px] font-semibold text-white shadow-glow transition hover:-translate-y-0.5 hover:bg-brand-600"
              >
                ورود به حساب
              </button>
            </form>

            <p className="mt-4 text-[12px] leading-6 text-ink-muted">
              پیش از ورود،{" "}
              <a href="/privacy" className="font-medium text-brand-600 underline-offset-4 hover:underline">
                حریم خصوصی
              </a>{" "}
              را بخوانید. اگر هنوز حساب ندارید، ثبت‌نام را از داخل برنامه انجام دهید.
            </p>
          </motion.div>
        )}
      </AnimatePresence>
    </section>
  );
}
