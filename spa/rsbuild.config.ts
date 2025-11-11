import { defineConfig } from '@rsbuild/core';
import { pluginReact } from '@rsbuild/plugin-react';

export default defineConfig({
  html: {
    template: "./src/index.html",
  },
  plugins: [pluginReact()],
});
