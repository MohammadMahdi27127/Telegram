"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Info, LockKeyhole, LogIn, UserRound, Loader2 } from "lucide-react";
import { adminLogin, setAdminSession } from "@/lib/admin-api";

export function AdminLoginForm() {
  const router = useRouter();
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function onSubmit(event: React.FormEvent) {
    event.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const data = await adminLogin(identifier.trim(), password);
      setAdminSession({
        token: data.token,
        name: data.name,
        phone: data.phone,
        user_id: data.user_id,
      });
      router.replace("/admin");
    } catch (e) {
      setError(e instanceof Error ? e.message : "ورود ناموفق بود");
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={onSubmit} className="card mx-auto max-w-md p-6 sm:p-7">
      <label className="block text-[12.5px] font-medium text-ink-soft" htmlFor="admin-id">
        شمارهٔ موبایل ادمین
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
          required
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
          required
        />
      </div>

      <button type="submit" className="btn-primary mt-6 w-full" disabled={loading}>
        {loading ? <Loader2 className="h-4 w-4 animate-spin" /> : <LogIn className="h-4 w-4" />}
        {loading ? "در حال ورود…" : "ورود به پنل مدیر"}
      </button>

      {error && (
        <p className="mt-4 flex items-start gap-2 rounded-2xl border border-red-100 bg-red-50/80 px-4 py-3 text-[12.5px] leading-7 text-red-700">
          <Info className="mt-1 h-4 w-4 shrink-0" />
          {error}
        </p>
      )}

      <p className="mt-4 text-center text-[11.5px] text-ink-faint">
        فقط شمارهٔ ادمین سیستم مجاز به ورود است.
      </p>
    </form>
  );
}
