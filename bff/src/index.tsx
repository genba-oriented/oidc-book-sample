import { StrictMode } from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import { TopPage } from "./pages/TopPage";

const root = document.getElementById("root");

ReactDOM.createRoot(root).render(
  <StrictMode>
    <TopPage />
  </StrictMode>
);