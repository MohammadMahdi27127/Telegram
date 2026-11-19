"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import {
  Bot,
  Check,
  CreditCard,
  Loader2,
  LogOut,
  MessageSquare,
  RefreshCw,
  Search,
  ShieldOff,
  ShieldCheck,
  Users,
  X,
  Send,
  Plus,
} from "lucide-react";
import {
  type AdminSlip,
  type AdminUser,
  type AiModel,
  type SupportMessage,
  type SupportThread,
  clearAdminSession,
  deleteAiModel,
  fetchAiModels,
  fetchSlips,
  fetchSupportMessages,
  fetchSupportThreads,
  fetchUsers,
  formatNumber,
  formatTs,
  getAdminMeta,
  getAdminToken,
  replySupport,
  setActiveAiModel,
  slipAction,
  suspendUser,
  upsertAiModel,
} from "@/lib/admin-api";

type Tab = "users" | "slips" | "support" | "ai";

export function AdminDashboard() {
  const router = useRouter();
  const [ready, setReady] = useState(false);
  const [tab, setTab] = useState<Tab>("users");
  const [metaName, setMetaName] = useState("");

  useEffect(() => {
    const token = getAdminToken();
    if (!token) {
      router.replace("/admin/login");
      return;
    }
    const meta = getAdminMeta();
    setMetaName(meta?.name || meta?.phone || "ادمین");
    setReady(true);
  }, [router]);

  if (!ready) {
    return (
      <div className="flex min-h-screen items-center justify-center text-ink-soft">
        <Loader2 className="h-6 w-6 animate-spin" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-canvas">
      <header className="border-b border-slate-200/80 bg-white/80 backdrop-blur-xl">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-5 py-4 sm:px-8">
          <div>
            <p className="text-[11px] font-medium text-brand-600">پنل مدیریت</p>
            <h1 className="text-lg font-bold text-ink">مکالمات من</h1>
          </div>
          <div className="flex items-center gap-3">
            <span className="hidden text-[12.5px] text-ink-soft sm:inline">{metaName}</span>
            <button
              type="button"
              className="btn-outline !px-3 !py-2 text-[12.5px]"
              onClick={() => {
                clearAdminSession();
                router.replace("/admin/login");
              }}
            >
              <LogOut className="h-3.5 w-3.5" />
              خروج
            </button>
          </div>
        </div>
        <nav className="mx-auto flex max-w-6xl gap-1 overflow-x-auto px-5 pb-3 sm:px-8">
          {(
            [
              { id: "users" as Tab, label: "کاربران", icon: Users },
              { id: "slips" as Tab, label: "فیش‌ها", icon: CreditCard },
              { id: "support" as Tab, label: "پشتیبانی", icon: MessageSquare },
              { id: "ai" as Tab, label: "مدل AI", icon: Bot },
            ] as const
          ).map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              type="button"
              onClick={() => setTab(id)}
              className={`inline-flex items-center gap-1.5 rounded-2xl px-4 py-2 text-[13px] font-medium transition ${
                tab === id
                  ? "bg-brand-500 text-white shadow-glow"
                  : "bg-white text-ink-soft hover:bg-brand-50 hover:text-brand-700"
              }`}
            >
              <Icon className="h-3.5 w-3.5" />
              {label}
            </button>
          ))}
        </nav>
      </header>

      <main className="mx-auto max-w-6xl px-5 py-6 sm:px-8">
        {tab === "users" && <UsersPanel />}
        {tab === "slips" && <SlipsPanel />}
        {tab === "support" && <SupportPanel />}
        {tab === "ai" && <AiPanel />}
      </main>
    </div>
  );
}

function UsersPanel() {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [totalTokens, setTotalTokens] = useState(0);
  const [query, setQuery] = useState("");
  const [plan, setPlan] = useState("all");
  const [sortBy, setSortBy] = useState("newest");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busyId, setBusyId] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchUsers({ query: query || undefined, plan, sort_by: sortBy });
      setUsers(data.users);
      setTotalTokens(data.total_tokens_used);
    } catch (e) {
      setError(e instanceof Error ? e.message : "خطا");
    } finally {
      setLoading(false);
    }
  }, [query, plan, sortBy]);

  useEffect(() => {
    void load();
  }, [load]);

  async function toggleSuspend(u: AdminUser) {
    setBusyId(u.id);
    try {
      await suspendUser(u.id, !u.suspended);
      await load();
    } catch (e) {
      alert(e instanceof Error ? e.message : "خطا");
    } finally {
      setBusyId(null);
    }
  }

  return (
    <section className="space-y-4">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <h2 className="text-base font-bold text-ink">کاربران ثبت‌نام‌شده</h2>
          <p className="mt-1 text-[12.5px] text-ink-soft">
            مجموع مصرف توکن: {formatNumber(totalTokens)} · تعداد: {formatNumber(users.length)}
          </p>
        </div>
        <button type="button" className="btn-outline !px-3 !py-2 text-[12.5px]" onClick={() => void load()}>
          <RefreshCw className={`h-3.5 w-3.5 ${loading ? "animate-spin" : ""}`} />
          بروزرسانی
        </button>
      </div>

      <div className="flex flex-wrap gap-2">
        <div className="flex min-w-[200px] flex-1 items-center gap-2 rounded-2xl border border-slate-200 bg-white px-3 py-2">
          <Search className="h-4 w-4 text-ink-faint" />
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="جستجو نام / شماره / شناسه"
            className="w-full bg-transparent text-[13px] outline-none"
          />
        </div>
        <select
          value={plan}
          onChange={(e) => setPlan(e.target.value)}
          className="rounded-2xl border border-slate-200 bg-white px-3 py-2 text-[13px]"
        >
          <option value="all">همه پلن‌ها</option>
          <option value="free">رایگان</option>
          <option value="plus">پلاس</option>
          <option value="smart">هوشمند</option>
          <option value="advanced">پیشرفته</option>
          <option value="guest">مهمان</option>
        </select>
        <select
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value)}
          className="rounded-2xl border border-slate-200 bg-white px-3 py-2 text-[13px]"
        >
          <option value="newest">جدیدترین</option>
          <option value="highest_usage">بیشترین مصرف</option>
          <option value="lowest_usage">کمترین مصرف</option>
        </select>
      </div>

      {error && <p className="rounded-2xl bg-red-50 px-4 py-3 text-[13px] text-red-700">{error}</p>}

      <div className="overflow-x-auto rounded-3xl border border-slate-200/80 bg-white shadow-soft">
        <table className="w-full min-w-[720px] text-right text-[13px]">
          <thead className="border-b border-slate-100 bg-slate-50/80 text-[11.5px] text-ink-muted">
            <tr>
              <th className="px-4 py-3 font-medium">کاربر</th>
              <th className="px-4 py-3 font-medium">پلن</th>
              <th className="px-4 py-3 font-medium">مصرف توکن</th>
              <th className="px-4 py-3 font-medium">AI ماهانه</th>
              <th className="px-4 py-3 font-medium">ثبت‌نام</th>
              <th className="px-4 py-3 font-medium">وضعیت</th>
              <th className="px-4 py-3 font-medium">عملیات</th>
            </tr>
          </thead>
          <tbody>
            {loading && users.length === 0 ? (
              <tr>
                <td colSpan={7} className="px-4 py-10 text-center text-ink-faint">
                  <Loader2 className="mx-auto h-5 w-5 animate-spin" />
                </td>
              </tr>
            ) : users.length === 0 ? (
              <tr>
                <td colSpan={7} className="px-4 py-10 text-center text-ink-faint">
                  کاربری یافت نشد
                </td>
              </tr>
            ) : (
              users.map((u) => (
                <tr key={u.id} className="border-b border-slate-50 last:border-0">
                  <td className="px-4 py-3">
                    <div className="font-medium text-ink">{u.name || "—"}</div>
                    <div className="text-[11.5px] text-ink-muted" dir="ltr">
                      {u.phone}
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <span className="rounded-full bg-brand-50 px-2.5 py-0.5 text-[11.5px] text-brand-700">
                      {u.plan}
                    </span>
                  </td>
                  <td className="px-4 py-3 tabular-nums">{formatNumber(u.tokens_used)}</td>
                  <td className="px-4 py-3 tabular-nums">{formatNumber(u.monthly_ai_usage_count)}</td>
                  <td className="px-4 py-3 text-[12px] text-ink-soft">{formatTs(u.created_at)}</td>
                  <td className="px-4 py-3">
                    {u.suspended ? (
                      <span className="text-[12px] text-red-600">معلق</span>
                    ) : (
                      <span className="text-[12px] text-emerald-600">فعال</span>
                    )}
                  </td>
                  <td className="px-4 py-3">
                    <button
                      type="button"
                      disabled={busyId === u.id}
                      onClick={() => void toggleSuspend(u)}
                      className="inline-flex items-center gap-1 rounded-xl border border-slate-200 px-2.5 py-1.5 text-[12px] hover:bg-slate-50 disabled:opacity-50"
                    >
                      {u.suspended ? (
                        <>
                          <ShieldCheck className="h-3.5 w-3.5 text-emerald-600" />
                          رفع تعلیق
                        </>
                      ) : (
                        <>
                          <ShieldOff className="h-3.5 w-3.5 text-red-500" />
                          تعلیق
                        </>
                      )}
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

function SlipsPanel() {
  const [slips, setSlips] = useState<AdminSlip[]>([]);
  const [status, setStatus] = useState("pending");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busyId, setBusyId] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setSlips(await fetchSlips(status));
    } catch (e) {
      setError(e instanceof Error ? e.message : "خطا");
    } finally {
      setLoading(false);
    }
  }, [status]);

  useEffect(() => {
    void load();
  }, [load]);

  async function act(slip: AdminSlip, action: "approve" | "reject") {
    setBusyId(slip.id);
    try {
      let userId = slip.user_id || undefined;
      if (action === "approve" && !userId) {
        const input = window.prompt("شناسه کاربر (user_id) برای این فیش را وارد کنید:");
        if (!input) {
          setBusyId(null);
          return;
        }
        userId = input.trim();
      }
      await slipAction(slip.id, {
        action,
        user_id: userId,
        plan: slip.plan || "plus",
        duration_days: 30,
      });
      await load();
    } catch (e) {
      alert(e instanceof Error ? e.message : "خطا");
    } finally {
      setBusyId(null);
    }
  }

  return (
    <section className="space-y-4">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <h2 className="text-base font-bold text-ink">فیش‌های پرداخت</h2>
          <p className="mt-1 text-[12.5px] text-ink-soft">تأیید فیش، پلن کاربر را فعال می‌کند</p>
        </div>
        <div className="flex gap-2">
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            className="rounded-2xl border border-slate-200 bg-white px-3 py-2 text-[13px]"
          >
            <option value="pending">در انتظار</option>
            <option value="approved">تأییدشده</option>
            <option value="rejected">ردشده</option>
            <option value="all">همه</option>
          </select>
          <button type="button" className="btn-outline !px-3 !py-2 text-[12.5px]" onClick={() => void load()}>
            <RefreshCw className={`h-3.5 w-3.5 ${loading ? "animate-spin" : ""}`} />
          </button>
        </div>
      </div>

      {error && <p className="rounded-2xl bg-red-50 px-4 py-3 text-[13px] text-red-700">{error}</p>}

      <div className="grid gap-3">
        {loading && slips.length === 0 ? (
          <div className="py-12 text-center text-ink-faint">
            <Loader2 className="mx-auto h-5 w-5 animate-spin" />
          </div>
        ) : slips.length === 0 ? (
          <p className="py-12 text-center text-[13px] text-ink-faint">فیشی نیست</p>
        ) : (
          slips.map((s) => (
            <div key={s.id} className="card flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
              <div className="space-y-1">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="font-medium text-ink">{s.user_name || s.user_phone || s.user_id || "کاربر نامشخص"}</span>
                  <span className="rounded-full bg-slate-100 px-2 py-0.5 text-[11px] text-ink-muted">{s.status}</span>
                  <span className="rounded-full bg-brand-50 px-2 py-0.5 text-[11px] text-brand-700">{s.plan || "—"}</span>
                </div>
                <p className="text-[12.5px] text-ink-soft">
                  مبلغ: {formatNumber(s.amount)} · پیگیری: {s.ref_code || "—"} · {formatTs(s.created_at)}
                </p>
                {s.receipt && (
                  <p className="max-w-xl break-all text-[11.5px] text-ink-muted" dir="ltr">
                    رسید: {s.receipt}
                  </p>
                )}
              </div>
              {s.status === "pending" && (
                <div className="flex shrink-0 gap-2">
                  <button
                    type="button"
                    disabled={busyId === s.id}
                    onClick={() => void act(s, "approve")}
                    className="inline-flex items-center gap-1 rounded-xl bg-emerald-500 px-3 py-2 text-[12.5px] font-medium text-white hover:bg-emerald-600 disabled:opacity-50"
                  >
                    <Check className="h-3.5 w-3.5" />
                    تأیید
                  </button>
                  <button
                    type="button"
                    disabled={busyId === s.id}
                    onClick={() => void act(s, "reject")}
                    className="inline-flex items-center gap-1 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-[12.5px] font-medium text-red-700 hover:bg-red-100 disabled:opacity-50"
                  >
                    <X className="h-3.5 w-3.5" />
                    رد
                  </button>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </section>
  );
}

function SupportPanel() {
  const [threads, setThreads] = useState<SupportThread[]>([]);
  const [selected, setSelected] = useState<string | null>(null);
  const [messages, setMessages] = useState<SupportMessage[]>([]);
  const [reply, setReply] = useState("");
  const [loading, setLoading] = useState(true);
  const [msgLoading, setMsgLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadThreads = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setThreads(await fetchSupportThreads());
    } catch (e) {
      setError(e instanceof Error ? e.message : "خطا");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadThreads();
  }, [loadThreads]);

  async function openThread(userId: string) {
    setSelected(userId);
    setMsgLoading(true);
    try {
      setMessages(await fetchSupportMessages(userId));
      await loadThreads();
    } catch (e) {
      alert(e instanceof Error ? e.message : "خطا");
    } finally {
      setMsgLoading(false);
    }
  }

  async function sendReply() {
    if (!selected || !reply.trim()) return;
    setSending(true);
    try {
      const msg = await replySupport(selected, reply.trim());
      setMessages((prev) => [...prev, msg]);
      setReply("");
      await loadThreads();
    } catch (e) {
      alert(e instanceof Error ? e.message : "خطا");
    } finally {
      setSending(false);
    }
  }

  const selectedMeta = useMemo(
    () => threads.find((t) => t.user_id === selected),
    [threads, selected],
  );

  return (
    <section className="grid gap-4 lg:grid-cols-[320px_1fr]">
      <div className="card overflow-hidden">
        <div className="flex items-center justify-between border-b border-slate-100 px-4 py-3">
          <h2 className="text-[13.5px] font-bold text-ink">تیکت‌های پشتیبانی</h2>
          <button type="button" onClick={() => void loadThreads()} className="text-ink-muted hover:text-brand-600">
            <RefreshCw className={`h-3.5 w-3.5 ${loading ? "animate-spin" : ""}`} />
          </button>
        </div>
        {error && <p className="px-4 py-2 text-[12px] text-red-600">{error}</p>}
        <div className="max-h-[520px] overflow-y-auto">
          {loading && threads.length === 0 ? (
            <div className="py-10 text-center">
              <Loader2 className="mx-auto h-5 w-5 animate-spin text-ink-faint" />
            </div>
          ) : threads.length === 0 ? (
            <p className="px-4 py-10 text-center text-[12.5px] text-ink-faint">تیکتی نیست</p>
          ) : (
            threads.map((t) => (
              <button
                key={t.user_id}
                type="button"
                onClick={() => void openThread(t.user_id)}
                className={`block w-full border-b border-slate-50 px-4 py-3 text-right transition hover:bg-brand-50/50 ${
                  selected === t.user_id ? "bg-brand-50" : ""
                }`}
              >
                <div className="flex items-center justify-between gap-2">
                  <span className="text-[13px] font-medium text-ink">{t.user_name || t.user_phone || t.user_id.slice(0, 8)}</span>
                  {t.unread_admin > 0 && (
                    <span className="rounded-full bg-brand-500 px-1.5 text-[10px] text-white">{t.unread_admin}</span>
                  )}
                </div>
                <p className="mt-0.5 line-clamp-1 text-[11.5px] text-ink-muted">{t.last_message || "—"}</p>
              </button>
            ))
          )}
        </div>
      </div>

      <div className="card flex min-h-[420px] flex-col">
        {!selected ? (
          <div className="flex flex-1 items-center justify-center text-[13px] text-ink-faint">
            یک گفتگو را انتخاب کنید
          </div>
        ) : (
          <>
            <div className="border-b border-slate-100 px-4 py-3">
              <p className="text-[13.5px] font-bold text-ink">
                {selectedMeta?.user_name || selectedMeta?.user_phone || selected}
              </p>
              <p className="text-[11.5px] text-ink-muted" dir="ltr">
                {selectedMeta?.user_phone}
              </p>
            </div>
            <div className="flex-1 space-y-2 overflow-y-auto px-4 py-3">
              {msgLoading ? (
                <Loader2 className="mx-auto my-8 h-5 w-5 animate-spin text-ink-faint" />
              ) : (
                messages.map((m) => (
                  <div
                    key={m.id}
                    className={`max-w-[85%] rounded-2xl px-3.5 py-2 text-[13px] leading-6 ${
                      m.from_admin
                        ? "mr-auto bg-brand-500 text-white"
                        : "ml-auto bg-slate-100 text-ink"
                    }`}
                  >
                    {m.body}
                    <div className={`mt-1 text-[10px] ${m.from_admin ? "text-white/70" : "text-ink-faint"}`}>
                      {formatTs(m.created_at)}
                    </div>
                  </div>
                ))
              )}
            </div>
            <div className="flex gap-2 border-t border-slate-100 p-3">
              <input
                value={reply}
                onChange={(e) => setReply(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" && !e.shiftKey) {
                    e.preventDefault();
                    void sendReply();
                  }
                }}
                placeholder="پاسخ شما…"
                className="flex-1 rounded-2xl border border-slate-200 px-3.5 py-2.5 text-[13px] outline-none focus:border-brand-300"
              />
              <button
                type="button"
                disabled={sending || !reply.trim()}
                onClick={() => void sendReply()}
                className="btn-primary !px-4 !py-2.5 disabled:opacity-50"
              >
                {sending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Send className="h-4 w-4" />}
              </button>
            </div>
          </>
        )}
      </div>
    </section>
  );
}

function AiPanel() {
  const [models, setModels] = useState<AiModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [busy, setBusy] = useState(false);
  const [form, setForm] = useState({
    name: "",
    endpoint: "https://openrouter.ai/api/v1/chat/completions",
    model_id: "",
    api_key: "",
    system_prompt: "",
    active: true,
  });

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setModels(await fetchAiModels());
    } catch (e) {
      setError(e instanceof Error ? e.message : "خطا");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  async function onSave() {
    setBusy(true);
    try {
      await upsertAiModel({
        name: form.name,
        endpoint: form.endpoint,
        model_id: form.model_id,
        api_key: form.api_key,
        system_prompt: form.system_prompt,
        active: form.active,
      });
      setShowForm(false);
      setForm({
        name: "",
        endpoint: "https://openrouter.ai/api/v1/chat/completions",
        model_id: "",
        api_key: "",
        system_prompt: "",
        active: true,
      });
      await load();
    } catch (e) {
      alert(e instanceof Error ? e.message : "خطا");
    } finally {
      setBusy(false);
    }
  }

  async function activate(id: string) {
    setBusy(true);
    try {
      await setActiveAiModel(id);
      await load();
    } catch (e) {
      alert(e instanceof Error ? e.message : "خطا");
    } finally {
      setBusy(false);
    }
  }

  async function remove(id: string) {
    if (!confirm("این مدل حذف شود؟")) return;
    setBusy(true);
    try {
      await deleteAiModel(id);
      await load();
    } catch (e) {
      alert(e instanceof Error ? e.message : "خطا");
    } finally {
      setBusy(false);
    }
  }

  return (
    <section className="space-y-4">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <h2 className="text-base font-bold text-ink">مدل‌های هوش مصنوعی</h2>
          <p className="mt-1 text-[12.5px] text-ink-soft">
            مدل فعال برای همه کاربران از مسیر سینک ارسال می‌شود
          </p>
        </div>
        <div className="flex gap-2">
          <button type="button" className="btn-outline !px-3 !py-2 text-[12.5px]" onClick={() => void load()}>
            <RefreshCw className={`h-3.5 w-3.5 ${loading ? "animate-spin" : ""}`} />
          </button>
          <button type="button" className="btn-primary !px-3 !py-2 text-[12.5px]" onClick={() => setShowForm((v) => !v)}>
            <Plus className="h-3.5 w-3.5" />
            مدل جدید
          </button>
        </div>
      </div>

      {error && <p className="rounded-2xl bg-red-50 px-4 py-3 text-[13px] text-red-700">{error}</p>}

      {showForm && (
        <div className="card grid gap-3 p-4 sm:grid-cols-2">
          <label className="block text-[12px] text-ink-soft">
            نام نمایشی
            <input
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-[13px]"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
            />
          </label>
          <label className="block text-[12px] text-ink-soft">
            شناسه مدل (model_id)
            <input
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-[13px]"
              value={form.model_id}
              onChange={(e) => setForm({ ...form, model_id: e.target.value })}
              dir="ltr"
            />
          </label>
          <label className="block text-[12px] text-ink-soft sm:col-span-2">
            Endpoint
            <input
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-[13px]"
              value={form.endpoint}
              onChange={(e) => setForm({ ...form, endpoint: e.target.value })}
              dir="ltr"
            />
          </label>
          <label className="block text-[12px] text-ink-soft sm:col-span-2">
            API Key
            <input
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-[13px]"
              value={form.api_key}
              onChange={(e) => setForm({ ...form, api_key: e.target.value })}
              dir="ltr"
            />
          </label>
          <label className="block text-[12px] text-ink-soft sm:col-span-2">
            System Prompt
            <textarea
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-[13px]"
              rows={3}
              value={form.system_prompt}
              onChange={(e) => setForm({ ...form, system_prompt: e.target.value })}
            />
          </label>
          <label className="flex items-center gap-2 text-[13px] text-ink-soft">
            <input
              type="checkbox"
              checked={form.active}
              onChange={(e) => setForm({ ...form, active: e.target.checked })}
            />
            فعال‌سازی فوری
          </label>
          <div className="flex justify-end gap-2 sm:col-span-2">
            <button type="button" className="btn-outline !py-2 text-[12.5px]" onClick={() => setShowForm(false)}>
              انصراف
            </button>
            <button
              type="button"
              disabled={busy || !form.name || !form.model_id || !form.api_key}
              className="btn-primary !py-2 text-[12.5px] disabled:opacity-50"
              onClick={() => void onSave()}
            >
              {busy ? <Loader2 className="h-4 w-4 animate-spin" /> : null}
              ذخیره
            </button>
          </div>
        </div>
      )}

      <div className="grid gap-3">
        {loading && models.length === 0 ? (
          <div className="py-12 text-center">
            <Loader2 className="mx-auto h-5 w-5 animate-spin text-ink-faint" />
          </div>
        ) : models.length === 0 ? (
          <p className="py-12 text-center text-[13px] text-ink-faint">مدلی ثبت نشده</p>
        ) : (
          models.map((m) => (
            <div key={m.id} className="card flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <div className="flex flex-wrap items-center gap-2">
                  <span className="font-medium text-ink">{m.name}</span>
                  {m.active && (
                    <span className="rounded-full bg-emerald-50 px-2 py-0.5 text-[11px] text-emerald-700">فعال</span>
                  )}
                </div>
                <p className="mt-1 text-[12px] text-ink-muted" dir="ltr">
                  {m.model_id} · {m.endpoint}
                </p>
              </div>
              <div className="flex gap-2">
                {!m.active && (
                  <button
                    type="button"
                    disabled={busy}
                    onClick={() => void activate(m.id)}
                    className="rounded-xl bg-brand-500 px-3 py-2 text-[12.5px] text-white hover:bg-brand-600 disabled:opacity-50"
                  >
                    فعال‌سازی
                  </button>
                )}
                <button
                  type="button"
                  disabled={busy}
                  onClick={() => void remove(m.id)}
                  className="rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-[12.5px] text-red-700 disabled:opacity-50"
                >
                  حذف
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </section>
  );
}
