import type { Metadata } from "next";
import { PageShell } from "@/components/page-shell";
import { AdminLoginForm } from "@/components/admin-login-form";

export const metadata: Metadata = {
  title: "ورود مدیر | مکالمات من",
  description: "ورود مدیران به پنل مکالمات من.",
  robots: { index: false, follow: false },
};

export default function AdminLoginPage() {
  return (
    <PageShell
      kicker="ورود مدیر"
      title="دسترسی مدیریتی"
      intro="برای بررسی رسیدهای اشتراک، مدیریت کاربران و پیام‌های پشتیبانی وارد شوید."
    >
      <AdminLoginForm />
    </PageShell>
  );
}
