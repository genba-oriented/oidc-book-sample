import { User } from "@/model/User";
import { UserProvider, useUser } from "@/model/UserProvider";
import { useEffect, useState } from "react";


export const TopPage = () => {
  const [user, setUser] = useState(null);
  useEffect(() => {
    fetch("/me").then(res => res.json()).then(data => {
      if (data.name != null) {
        setUser(new User(true, data.name));
      } else {
        setUser(new User(false, null));
      }
    });
  }, []);

  if (user == null) {
    return;
  }
  return (
    <UserProvider value={user}>
      <TopPageContent />
    </UserProvider>
  );
};

const TopPageContent = () => {
  const user = useUser();
  return (
    <div>
      <h1>トップ画面</h1>
      {user.isAuthenticated ? <AfterLogin /> : <BeforeLogin />}
    </div>
  );
};

const BeforeLogin = () => {
  return (
    <div>
      <p>ログインしてください</p>
      <button onClick={() => {
        location.href = "/login";
      }}>ログイン</button>
    </div>
  );
};

const AfterLogin = () => {
  const user = useUser();
  const [orderItems, setOrderItems] = useState([]);
  useEffect(() => {
    fetch("/api/order-items").then(res => res.json()).then(data => setOrderItems(data));
  }, []);
  return (
    <div>
      <p>こんにちは、{user.name}さん！</p>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>商品名</th>
            <th>数量</th>
            <th>値段</th>
          </tr>
        </thead>
        <tbody>
          {orderItems.map((orderItem, idx) =>
            <tr key={idx}>
              <td>{orderItem.id}</td>
              <td>{orderItem.productName}</td>
              <td>{orderItem.quantity}</td>
              <td>{orderItem.price}</td>
            </tr>
          )}
        </tbody>
      </table>
      <button onClick={() => {
        location.href = "/logout";
      }}>ログアウト</button>
    </div>
  );
};
