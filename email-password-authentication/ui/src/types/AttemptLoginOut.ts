export type AttemptLoginOut = FailureAttemptLoginOut | SuccessAttemptLoginOut | RecoverAccountAttemptLoginOut;

export type FailureAttemptLoginOut = {
  status: "FAILURE";
  info: {
    attemptsLeft: number;
  };
};

export type SuccessAttemptLoginOut = {
  status: "SUCCESS";
  info: {
    clientSessionId: string;
    loginCode: string;
  };
};

export type RecoverAccountAttemptLoginOut = {
  status: "RECOVER_ACCOUNT";
};
