import { createContext, useContext } from "react";
import { User } from "./User";

const Context = createContext<User>(null);

export const UserProvider = ({
  value,
  children,
}: {
  value: User
  children: React.ReactNode
}) => {
  return (
    <Context.Provider value={value}>
      {children}
    </Context.Provider>
  )
}

export function useUser() {
  return useContext(Context);
}