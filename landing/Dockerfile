FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM node:18-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production

# ایجاد پوشه downloads جهت تضمین وجود مسیر و عدم وقوع خطای کپی در داکر
RUN mkdir -p downloads

# کپی کردن پوشه downloads حاوی فایل نصبی
COPY --from=builder /app/downloads ./downloads

COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static

EXPOSE 3000
ENV PORT=3000
CMD ["node", "server.js"]
