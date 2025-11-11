import { defineConfig } from '@rsbuild/core';
import { pluginReact } from '@rsbuild/plugin-react';
import { app } from "./src-server/bff";

export default defineConfig({

  html: {
    template: "./src/index.html",
  },
  plugins: [pluginReact()],
  dev: {
    setupMiddlewares: [
      (middlewares) => {
        middlewares.unshift(app);
      }
    ]
  }
});
