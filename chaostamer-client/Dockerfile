FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install --production
COPY . .
RUN npm run build
CMD ["serve", "-s", "dist, "-l", "5173"]
EXPOSE 5173