import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  base: "./",
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          // compared using source-map-explorer
          zxcvbn: ["zxcvbn"],
        },
      },
    },
    chunkSizeWarningLimit: 1000,
  },
  server: {
    proxy: {
      "/api": {
        target: "https://extension.dev-in.eng.krista.app/extension/api/3W7R1iDhil_sButCCk3LP3Q_e_e/rest",
        changeOrigin: true,
        secure: false,
        configure: (proxy, _options) => {
          proxy.on("error", (err, _req, _res) => {
            console.log("proxy error", err);
          });
          proxy.on("proxyReq", (proxyReq, req, _res) => {
            console.log("Sending Request to the Target:", req.method, req.url);
          });
          proxy.on("proxyRes", (proxyRes, req, _res) => {
            console.log("Received Response from the Target:", proxyRes.statusCode, req.url);
          });
        },
      },
    },
  },
});
