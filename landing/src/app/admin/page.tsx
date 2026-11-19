import type { Metadata } from "next";
import { AdminDashboard } from "@/components/admin-dashboard";

export const metadata: Metadata = {
  title: "پنل مدیریت | مکالمات من",
  description: "مدیریت کاربران، فیش‌ها، پشتیبانی و مدل‌های هوش مصنوعی",
  robots: { index: false, follow: false },
};

export default function AdminPage() {
  return <AdminDashboard />;
}
