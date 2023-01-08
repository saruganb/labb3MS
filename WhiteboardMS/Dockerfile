FROM node:16.14.2

WORKDIR /app
ENV PATH /node_modules/.bin:$PATH

COPY package.json ./
COPY package-lock.json ./
RUN npm install

COPY . .

EXPOSE 2000

VOLUME [ "/app/node_modules" ]

CMD ["npm", "start"]