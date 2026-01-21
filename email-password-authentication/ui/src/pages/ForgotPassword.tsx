import { ChangeEvent, useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import { Header } from "../components/Header";
import { TermsAndConditions } from "../components/TermsAndConditions";
import { DataContext, contextSubject } from "../context";
import { routes } from "../constants/routes";
import { apiSendForgotLink } from "../api/FetchCalls";
import { goBack } from "../utils";

export function ForgotPassword() {
  const { t } = useTranslation();
  const context = useContext(DataContext);
  const [email, setEmail] = useState<string>("");

  useEffect(() => {
    setEmail(context.email || "");
  }, [context]);

  function onSubmitEmail(e: ChangeEvent<HTMLFormElement>) {
    e.preventDefault();
    apiSendForgotLink({ emailId: email, redirectUrl: context.redirectUrl }, () => {
      contextSubject.next({ ...context, email });
      context.navigate(routes.emailSend);
    });
  }

  return (
    <div className="h-screen w-screen flex justify-center text-text-primary">
      <div className="my-auto">
        <div className="shadow-xl p-10 rounded-xl bg-white">
          <div className="w-[320px]">
            <Header hideEmail />
            <form className="mt-6" onSubmit={onSubmitEmail}>
              <div className="flex flex-col">
                <div className="flex justify-between mb-1">
                  <label className="text-text-secondary text-xs" htmlFor="email">
                    {t("YOUR_EMAIL_ADDRESS")} <span className="text-danger">*</span>
                  </label>
                </div>
                <input
                  className="text-text-primary text-sm border border-solid border-gray-200 rounded p-[10px]"
                  type="email"
                  id="email"
                  name="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
              <div className="mt-4">
                <button className="text-white p-2 text-sm w-full block bg-primary rounded" type="submit">
                  {t("SUBMIT")}
                </button>
              </div>
            </form>
            <button onClick={goBack} type="button" className="text-xs text-text-secondary">
              {t("BACK")}
            </button>
          </div>
        </div>
        <TermsAndConditions />
      </div>
    </div>
  );
}
