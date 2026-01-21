import { useTranslation } from "react-i18next";

import { Header } from "./Header";
import { TermsAndConditions } from "./TermsAndConditions";
import { useEffect, useState } from "react";
import { PasswordStengthMeter } from "./PasswordStrengthMeter";
import { PasswordRequirements } from "./PasswordRequirements";
import { PasswordSubmit } from "../types/PasswordSubmit";
import zxcvbn from "zxcvbn";
import { goBack } from "../utils";

export function PasswordSet({
  submit,
  showOldPassword,
  backLink,
}: {
  submit: (input: PasswordSubmit) => any;
  showOldPassword?: boolean;
  backLink: boolean;
}) {
  const { t } = useTranslation();
  const [strength, setStrength] = useState(-1);
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isWarning, setIsWarning] = useState(false);
  const [informText, setInformText] = useState("");
  const [disableSubmit, setDisableSubmit] = useState(true);

  const [conditions, setConditions] = useState({
    minimumChar: false,
    upperCase: false,
    lowerCase: false,
    minimumNumber: false,
    minimumSpecialChar: false,
  });

  useEffect(() => {
    const newConditions = {
      minimumChar: newPassword.length >= 8,
      upperCase: newPassword.match(getPasswordOneUppercaseAlphaRegex()) !== null,
      lowerCase: newPassword.match(getPasswordOneLowercaseAlphaRegex()) !== null,
      minimumNumber: newPassword.match(getPasswordOneNumberRegex()) !== null,
      minimumSpecialChar: newPassword.match(getPasswordOneSpecialCharRegex()) !== null,
    };
    setConditions(newConditions);
  }, [newPassword]);

  const onSubmit = (e: any) => {
    e.preventDefault();
    const newPassword = e.target.elements["new-password"].value;
    const oldPassword = e.target.elements["old-password"]?.value;
    submit({ newPassword, oldPassword });
  };
  useEffect(() => {
    const result = zxcvbn(newPassword);
    setInformText(result.feedback.suggestions[0]);
    let newStrength = 0;
    if (newPassword.match(getPasswordRegex()) != null) {
      newStrength = result.score;
      if (result.feedback.warning) {
        setIsWarning(true);
        setInformText(result.feedback.warning);
      } else {
        setInformText("");
        setIsWarning(false);
      }
    } else if (newPassword.length == 0) {
      setIsWarning(false);
      newStrength = -1;
    } else {
      setIsWarning(false);
      newStrength = 0;
    }
    setStrength(newStrength);
  }, [newPassword]);

  useEffect(() => {
    setDisableSubmit(!(Object.values(conditions).every((x) => x) && newPassword === confirmPassword));
  }, [conditions, newPassword, confirmPassword]);

  return (
    <div className="h-screen w-screen flex justify-center text-text-primary">
      <div className="my-auto">
        <div className="shadow-xl p-10 rounded-xl bg-white">
          <div className="w-[320px]">
            <Header />
            <form className="mt-8" onSubmit={onSubmit}>
              {showOldPassword ? (
                <div className="flex flex-col mt-4">
                  <div className="flex justify-between mb-1">
                    <label className="text-text-secondary text-xs" htmlFor="new-password">
                      {t("OLD_PASSWORD")} <span className="text-danger">*</span>
                    </label>
                  </div>
                  <input
                    className="text-text-primary text-sm border border-solid border-gray-200 rounded p-[10px]"
                    type="password"
                    id="old-password"
                    name="old-password"
                    placeholder={t("OLD_PASSWORD")}
                    required
                  />
                </div>
              ) : null}
              <div className="flex flex-col mt-4">
                <div className="flex justify-between mb-1">
                  <label className="text-text-secondary text-xs" htmlFor="new-password">
                    {t("NEW_PASSWORD")} <span className="text-danger">*</span>
                  </label>
                </div>
                <input
                  className="text-text-primary text-sm border border-solid border-gray-200 rounded p-[10px]"
                  type="password"
                  id="new-password"
                  name="new-password"
                  placeholder={t("NEW_PASSWORD")}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                />
              </div>
              <div className="flex flex-col mt-4">
                <div className="flex justify-between mb-1">
                  <label className="text-text-secondary text-xs" htmlFor="confirm-password">
                    {t("CONFIRM_PASSWORD")} <span className="text-danger">*</span>
                  </label>
                </div>
                <input
                  className="text-text-primary text-sm border border-solid border-gray-200 rounded p-[10px]"
                  type="password"
                  id="confirm-password"
                  name="confirm-password"
                  placeholder={t("CONFIRM_PASSWORD")}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                />
              </div>
              <p className="text-xs text-text-secondary mt-4">
                {informText ? (
                  <>
                    {isWarning ? t("WARNING") : t("SUGGESTIONS")}: {informText}
                  </>
                ) : null}
              </p>
              <div className="mt-4">
                <PasswordStengthMeter strength={strength} />
              </div>
              <div className="mt-5">
                <PasswordRequirements conditions={conditions} />
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
            {backLink ? (
              <button onClick={goBack} type="button" className="text-xs text-text-secondary">
                {t("BACK")}
              </button>
            ) : null}
          </div>
        </div>
        <TermsAndConditions />
      </div>
    </div>
  );
}

function getPasswordRegex(): RegExp {
  return new RegExp(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*#?&]).{8,}$/);
}

function getPasswordOneUppercaseAlphaRegex(): RegExp {
  return new RegExp(/(^(?=.*[A-Z]))/);
}
function getPasswordOneLowercaseAlphaRegex(): RegExp {
  return new RegExp(/(^(?=.*[a-z]))/);
}

function getPasswordOneNumberRegex(): RegExp {
  return new RegExp(/(^(?=.*[0-9]))/);
}

function getPasswordOneSpecialCharRegex(): RegExp {
  return new RegExp(/(^(?=.*[@$!%*#?&]))/);
}
