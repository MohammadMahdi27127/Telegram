const API_BASE = typeof window !== "undefined" ? "/api/v1" : "/api/v1";

const TOKEN_KEY = "hs_admin_token";
const ADMIN_META_KEY = "hs_admin_meta";

export type AdminMeta = {
  token: string;
  name?: string;
  phone?: string;
  user_id?: string;
};

export function getAdminToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(TOKEN_KEY);
}

export function getAdminMeta(): AdminMeta | null {
  if (typeof window === "undefined") return null;
  const raw = localStorage.getItem(ADMIN_META_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AdminMeta;
  } catch {
    return null;
  }
}

export function setAdminSession(meta: AdminMeta) {
  localStorage.setItem(TOKEN_KEY, meta.token);
  localStorage.setItem(ADMIN_META_KEY, JSON.stringify(meta));
}

export function clearAdminSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(ADMIN_META_KEY);
}

async function adminFetch<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const token = getAdminToken();
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string> | undefined),
  };
  if (token) headers["X-Admin-Token"] = token;

  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (res.status === 401) {
    clearAdminSession();
    if (typeof window !== "undefined" && !window.location.pathname.includes("/admin/login")) {
      window.location.href = "/admin/login";
    }
    throw new Error("نشست ادمین منقضی شده است");
  }

  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const detail = (data as { detail?: string }).detail || res.statusText;
    throw new Error(typeof detail === "string" ? detail : "خطای سرور");
  }
  return data as T;
}

export async function adminLogin(phone: string, password: string) {
  const res = await fetch(`${API_BASE}/admin/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ phone, password }),
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new Error((data as { detail?: string }).detail || "ورود ناموفق");
  }
  return data as {
    status: string;
    token: string;
    name?: string;
    phone: string;
    user_id?: string;
  };
}

export type AdminUser = {
  id: string;
  phone: string;
  name?: string | null;
  username?: string | null;
  plan: string;
  balance: number;
  tokens_used: number;
  monthly_ai_usage_count: number;
  suspended: boolean;
  created_at?: number | null;
  plan_expires_at?: number | null;
};

export async function fetchUsers(params?: {
  query?: string;
  sort_by?: string;
  plan?: string;
}) {
  const q = new URLSearchParams();
  if (params?.query) q.set("query", params.query);
  if (params?.sort_by) q.set("sort_by", params.sort_by);
  if (params?.plan) q.set("plan", params.plan);
  const qs = q.toString() ? `?${q}` : "";
  return adminFetch<{ total_tokens_used: number; total_users: number; users: AdminUser[] }>(
    `/admin/users-stats${qs}`,
  );
}

export async function suspendUser(userId: string, suspended: boolean) {
  return adminFetch(`/admin/users/${userId}/suspend`, {
    method: "POST",
    body: JSON.stringify({ suspended }),
  });
}

export type AdminSlip = {
  id: string;
  user_id?: string | null;
  user_name?: string | null;
  user_phone?: string | null;
  kind?: string | null;
  plan?: string | null;
  amount: number;
  status: string;
  receipt?: string | null;
  ref_code?: string | null;
  created_at?: number | null;
};

export async function fetchSlips(status = "all") {
  return adminFetch<AdminSlip[]>(`/admin/slips?status=${encodeURIComponent(status)}`);
}

export async function slipAction(
  slipId: string,
  body: { action: "approve" | "reject"; user_id?: string; plan?: string; duration_days?: number },
) {
  return adminFetch(`/admin/slips/${slipId}/action`, {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export type SupportThread = {
  user_id: string;
  user_name?: string | null;
  user_phone?: string | null;
  last_message?: string | null;
  last_at?: number | null;
  unread_admin: number;
  total_messages: number;
};

export type SupportMessage = {
  id: string;
  user_id: string;
  from_admin: boolean;
  body: string;
  kind?: string;
  path?: string | null;
  read_admin: boolean;
  read_user: boolean;
  created_at?: number | null;
};

export async function fetchSupportThreads() {
  return adminFetch<SupportThread[]>("/admin/support/threads");
}

export async function fetchSupportMessages(userId: string) {
  return adminFetch<SupportMessage[]>(`/admin/support/${userId}/messages`);
}

export async function replySupport(userId: string, body: string) {
  return adminFetch<SupportMessage>(`/admin/support/${userId}/reply`, {
    method: "POST",
    body: JSON.stringify({ body, kind: "text" }),
  });
}

export type AiModel = {
  id: string;
  name?: string | null;
  endpoint?: string | null;
  model_id?: string | null;
  api_key?: string | null;
  input_cost: number;
  output_cost: number;
  system_prompt?: string | null;
  active: boolean;
  created_at?: number | null;
};

export async function fetchAiModels() {
  return adminFetch<AiModel[]>("/admin/ai-models");
}

export async function upsertAiModel(payload: {
  id?: string;
  name: string;
  endpoint: string;
  model_id: string;
  api_key: string;
  input_cost?: number;
  output_cost?: number;
  system_prompt?: string;
  active?: boolean;
}) {
  return adminFetch<AiModel>("/admin/ai-models", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export async function setActiveAiModel(modelId: string) {
  return adminFetch("/admin/ai-models/set-active", {
    method: "POST",
    body: JSON.stringify({ model_id: modelId }),
  });
}

export async function deleteAiModel(modelId: string) {
  return adminFetch(`/admin/ai-models/${modelId}`, { method: "DELETE" });
}

export function formatTs(ms?: number | null) {
  if (!ms) return "—";
  try {
    return new Date(ms).toLocaleString("fa-IR");
  } catch {
    return String(ms);
  }
}

export function formatNumber(n: number) {
  return new Intl.NumberFormat("fa-IR").format(n);
}
