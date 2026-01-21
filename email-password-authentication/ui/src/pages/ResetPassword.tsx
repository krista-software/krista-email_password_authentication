import { PasswordSet } from "../components/PasswordSet";
import { PasswordSubmit } from "../types/PasswordSubmit";
import { apiResetPassword } from "../api/FetchCalls";
import { useSearchParams } from "react-router-dom";

export function ResetPassword() {
  const [searchParams] = useSearchParams();
  const code = searchParams.get("code") || "";
  const submit = ({ newPassword }: PasswordSubmit) => {
    apiResetPassword(
      {
        newPassword,
        code,
      },
      () => (window.location.href = window.location.origin)
    );
  };
  return <PasswordSet submit={submit} showOldPassword={false} backLink={false} />;
}
