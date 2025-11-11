// サーバにデプロイして動かすときに使用します。
// npm run build
// npx tsx src-server/server.ts

import express, { Request, Response } from "express";

const app = express();
const baseDir = process.cwd();

app.use(express.static(baseDir + "/dist"));
app.get('/{*splat}', (req: Request, res: Response) => {
  res.sendFile(baseDir + "/dist/index.html");
});

const port = 3000;

app.listen(port, (error) => {
  if (error != null) {
    console.error(error);
    return;
  }
  console.log(`Server is running at port ${port}`);
});

