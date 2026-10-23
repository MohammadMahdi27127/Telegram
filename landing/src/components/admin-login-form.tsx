"use client";

import { useState } from "react";
import { Info, LockKeyhole, LogIn, UserRound } from "lucide-react";

export function AdminLoginForm() {
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [notice, setNotice] = useState(false);

  return (
    <form
      onSubmit={(event) => {
        event.preventDefault();
        setNotice(true);
      }}
      className="card mx-auto max-w-md p-6 sm:p-7"
    >
      <label className="block text-[12.5px] font-medium text-ink-soft" htmlFor="admin-id">
        شمارهٔ موبایل یا نام کاربری
      </label>
      <div className="mt-2 flex items-center gap-2 rounded-2xl border border-slate-200 bg-white px-3.5 py-3 transition focus-within:border-brand-300">
        <UserRound className="h-4 w-4 text-ink-faint" />
        <input
          id="admin-id"
          value={identifier}
          onChange={(event) => setIdentifier(event.target.value)}
          autoComplete="username"
          className="w-full bg-transparent text-[13.5px] text-ink outline-none placeholder:text-ink-faint"
          placeholder="۰۹۱۲…"
        />
      </div>

      <label className="mt-5 block text-[12.5px] font-medium text-ink-soft" htmlFor="admin-pass">
        رمز عبور
      </label>
      <div className="mt-2 flex items-center gap-2 rounded-2xl border border-slate-200 bg-white px-3.5 py-3 transition focus-within:border-brand-300">
        <LockKeyhole className="h-4 w-4 text-ink-faint" />
        <input
          id="admin-pass"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="current-password"
          className="w-full bg-transparent text-[13.5px] text-ink outline-none placeholder:text-ink-faint"
          placeholder="••••••••"
        />
      </div>

      <button type="submit" className="btn-primary mt-6 w-full">
        <LogIn className="h-4 w-4" />
        ورود به پنل مدیر
      </button>

      {notice && (
        <p className="mt-4 flex items-start gap-2 rounded-2xl border border-brand-100 bg-brand-50/70 px-4 py-3 text-[12.5px] leading-7 text-brand-700">
          <Info className="mt-1 h-4 w-4 shrink-0" />
          در حال حاضر پنل مدیر از داخل برنامهٔ اندروید در دسترس است؛ این صفحه فقط برای دسترسی سریع در وب آماده شده است.
        </p>
      )}
    </form>
  );
}
