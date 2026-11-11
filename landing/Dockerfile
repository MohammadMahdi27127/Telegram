FROM node:22-alpine AS builder

WORKDIR /app

COPY package*.json ./

# A lockfile is not currently tracked in this repository. Bootstrap one in the
# build layer, then install from it with npm ci. Commit a generated package-lock.json
# in a follow-up so dependency resolution is reproducible across separate builds.
RUN if [ -f package-lock.json ]; then \
      npm ci --no-audit --no-fund; \
    else \
      npm install --package-lock-only --ignore-scripts --no-audit --no-fund \
      && npm ci --no-audit --no-fund; \
    fi

COPY . .
RUN npm run build

FROM node:22-alpine AS runner

WORKDIR /app
ENV NODE_ENV=production \
    PORT=3000 \
    HOSTNAME=0.0.0.0

# next.config.mjs already enables output: 'standalone'.
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static

EXPOSE 3000

CMD ["node", "server.js"]
