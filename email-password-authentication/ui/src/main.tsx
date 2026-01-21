import React from "react";
import ReactDOM from "react-dom/client";
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import { App } from "./App.tsx";
import { routes } from "./constants/routes";
import { Password } from "./pages/Password";
import { NotFound } from "./pages/NotFound";
import { ForgotPassword } from "./pages/ForgotPassword";
import { AccountBlocked } from "./pages/AccountBlocked";
import { ResetPassword } from "./pages/ResetPassword";
import { ChangePassword } from "./pages/ChangePassword";
import "./locale/i18n.ts";
import "./index.css";
import { EmailSend } from "./pages/EmailSend.tsx";
import { getUIPath } from "./utils.ts";
import { MessagePage } from "./pages/MessagePage.tsx";

const router = createBrowserRouter([
  {
    element: <App />,
    path: getUIPath(),
    children: [
      {
        path: "password",
        element: <Password />,
      },
      {
        path: routes.forgotPassword,
        element: <ForgotPassword />,
      },
      {
        path: routes.accountBlocked,
        element: <AccountBlocked />,
      },
      {
        path: routes.resetPassword,
        element: <ResetPassword />,
      },
      {
        path: routes.changePassword,
        element: <ChangePassword />,
      },
      {
        path: routes.emailSend,
        element: <EmailSend />,
      },
      {
        path: routes.showMessage,
        element: <MessagePage />,
      },
    ],
  },
  {
    path: "*",
    element: <NotFound />,
  },
]);

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
