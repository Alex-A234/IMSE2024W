import Header from './components/Header';
import React, { useState, useEffect } from 'react';
import api from './router/router';


function App() {
  const [users, setUsers] = useState([]);
  const [currentUser, setCurrentUser] = useState(0);
  const [mainView, setMainView] = useState();

  useEffect(() => {
    api.path.getUsers().then(res => {
      setUsers(res.data);
    })
  }, [currentUser]);

  return (
    <div>
      <div class="h-20 w-full">
        <Header
          users={users}
          currentUser={currentUser}
          setCurrentUser={setCurrentUser}
          setMainView={setMainView}
        />
      </div>
      <div className="App">
        <div>
          <div>
            {mainView}
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
