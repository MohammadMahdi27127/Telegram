const FA_DIGITS = ["۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"];

/** تبدیل عدد به رشتهٔ فارسی با جداکنندهٔ هزارگان فارسی. */
export function toFa(value: number): string {
  return value
    .toLocaleString("en-US")
    .replace(/,/g, "٬")
    .replace(/\d/g, (d) => FA_DIGITS[Number(d)]);
}
