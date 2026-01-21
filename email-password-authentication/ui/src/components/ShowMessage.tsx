import { useTranslation } from "react-i18next";

import { Header } from "./Header";
import { TermsAndConditions } from "./TermsAndConditions";
import { goBack } from "../utils";

export function ShowMessage(props: { message: string; buttonText?: string; onSubmit?: () => any }) {
  const { t } = useTranslation();
  return (
    <div className="h-screen w-screen flex justify-center text-text-primary">
      <div className="my-auto">
        <div className="shadow-xl p-10 rounded-xl bg-white">
          <div className="w-[320px]">
            <div className="flex flex-col items-center">
              <Header />
              <p className="text-sm text-text-primary my-4 text-center">{t(props.message)}</p>
              <SubmitButton buttonText={props.buttonText} onSubmit={props.onSubmit} />
            </div>
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

function SubmitButton(props: { buttonText?: string; onSubmit?: () => any }) {
  const { t } = useTranslation();
  return props.buttonText ? (
    <button onClick={props.onSubmit} type="button" className="text-white text-sm py-2 px-4 block bg-primary rounded">
      {t(props.buttonText)}
    </button>
  ) : null;
}
