FROM node:20-alpine

WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install

COPY prisma ./prisma
RUN npx prisma generate

COPY src ./src
COPY protocols ./protocols

EXPOSE 8082
ENV PORT=8082
CMD ["npm", "run", "start"]
