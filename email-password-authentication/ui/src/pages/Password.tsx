import { useTranslation } from "react-i18next";
import { Header } from "../components/Header";
import { TermsAndConditions } from "../components/TermsAndConditions";
import { routes } from "../constants/routes";
import { useContext, useEffect, useState } from "react";
import { DataContext } from "../context";
import { AttemptLoginIn } from "../types/AttemptLoginIn";
import { callApi, apiIsUserBlocked } from "../api/FetchCalls";
import { apiCodes } from "../constants/apiCodes";
import { AttemptLoginOut } from "../types/AttemptLoginOut";
import { getSessionType } from "../types/SessionType";
import { AUTH_INFO_COOKIE_IN, AUTH_INFO_COOKIE_OUT, getCookie, goBack, setCookie } from "../utils";
import { AuthInfo, AuthInfoOut } from "../types/AuthInfo";
import { showError } from "../components/SnackBarComponent";

export function Password() {
  const { t } = useTranslation();
  const [errorText, setErrorText] = useState("");
  const context = useContext(DataContext);
  const [password, setPassword] = useState("");
  const [disableSubmit, setDisableSubmit] = useState(false);
  const [authInput, setAuthInput] = useState(null as AuthInfo | null);

  const updateAttemptError = (n: number) => {
    setErrorText(`${n} ${t("ATTEMPTS_LEFT")}`);
  };

  useEffect(() => {
    const authInfoCookie = getCookie(AUTH_INFO_COOKIE_IN);
    if (authInfoCookie) {
      const json = atob(authInfoCookie);
      setAuthInput(JSON.parse(json));
    } else {
      console.error("input cookie not found");
      setTimeout(() => context.navigate(routes.notFound), 1000);
    }
  }, []);

  useEffect(() => {
    const subscription = context.onChange.subscribe((context) => {
      if (!(context.email && context.redirectUrl)) {
        context.navigate(routes.notFound, false);
        return;
      }
      apiIsUserBlocked(context.email, ({ isBlocked }) => {
        if (isBlocked) {
          context.navigate(routes.accountBlocked);
        }
      });
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    setDisableSubmit(!password);
  }, [password]);

  const onSubmitPassword = (e: any) => {
    e.preventDefault();
    console.log([e]);
    const logindata: AttemptLoginIn = {
      password: e.target.elements.password.value,
      emailId: context.email as string,
      clientSessionId: authInput?.clientSessionId,
      sessionType: getSessionType(),
      loginCode: authInput?.loginCode,
    };
    const handleResponse = (o: AttemptLoginOut) => {
      if (o.status === "FAILURE") {
        showError(t("PASSWORD_IS_INCORRECT"));
        updateAttemptError(o.info.attemptsLeft);
      } else if (o.status === "RECOVER_ACCOUNT") {
        context.navigate(routes.accountBlocked);
      } else if (o.status === "SUCCESS") {
        const info = o.info;
        const output: AuthInfoOut = { ...info, workspaceId: context.workspaceInfo!.workspaceId };
        const encodedInfo = btoa(JSON.stringify(output));
        setCookie(AUTH_INFO_COOKIE_OUT, encodedInfo);
        context.navigate(context.redirectUrl);
      }
    };
    callApi(apiCodes.attemptLogin, "POST", logindata, handleResponse);
  };

  const forgetPassword = () => {
    context.navigate(routes.forgotPassword);
  };

  return (
    <>
      <div className="h-screen w-screen flex justify-center text-text-primary">
        <div className="my-auto">
          <div className="shadow-xl p-10 rounded-xl bg-white">
            <div className="w-[320px]">
              <Header />
              <form className="mt-8" onSubmit={onSubmitPassword}>
                <div className="flex flex-col">
                  <div className="flex justify-between mb-1">
                    <label className="text-text-secondary text-xs" htmlFor="password">
                      {t("PASSWORD")} <span className="text-danger">*</span>
                    </label>
                    {errorText ? <span className="text-danger text-xs">{errorText}</span> : null}
                  </div>
                  <input
                    onChange={(e) => setPassword(e.target.value)}
                    className="text-text-primary text-sm border border-solid border-gray-200 rounded p-[10px]"
                    type="password"
                    id="password"
                    name="password"
                    placeholder={t("PASSWORD")}
                    required
                  />
                </div>
                <div className="mt-4">
                  <button
                    disabled={disableSubmit}
                    className="text-white text-sm p-2 w-full block bg-primary rounded"
                    type="submit"
                  >
                    {t("SUBMIT")}
                  </button>
                </div>
              </form>
              <div className="mt-4 flex justify-between">
                <button onClick={goBack} type="button" className="text-xs text-text-secondary">
                  {t("BACK")}
                </button>
                <button onClick={forgetPassword} type="button" className="text-xs text-primary">
                  {t("FORGOT_PASSWORD")}
                </button>
              </div>
            </div>
          </div>
          <TermsAndConditions />
        </div>
      </div>
    </>
  );
}
