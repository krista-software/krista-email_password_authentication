import { apiChangePassword, apiIsUserBlocked } from "../api/FetchCalls";
import { PasswordSet } from "../components/PasswordSet";
import { PasswordSubmit } from "../types/PasswordSubmit";
import { useContext, useEffect } from "react";
import { DataContext } from "../context";
import { routes } from "../constants/routes";
import { getServerRoot } from "../utils";

export function ChangePassword() {
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

  const context = useContext(DataContext);
  const submit = ({ newPassword, oldPassword }: PasswordSubmit) => {
    apiChangePassword({ newPassword, oldPassword: oldPassword || "", emailId: context.email as string }, () =>
      context.navigate(context.redirectUrl || getServerRoot())
    );
  };
  return <PasswordSet submit={submit} showOldPassword={true} backLink={true} />;
}
