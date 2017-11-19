﻿
using UnityEngine.SceneManagement;

public class NavigationManager{

    public const string forgotPassword = "forgotPassword";
    public const string main = "MAIN";
    public const string login = "login";
    public const string sceneMenu = "SceneMenu";
    public const string leaderboard = "leaderboard";
    public const string achivement = "achivement";
    public const string settings = "SettingMenu";
    public const string game = "GameMenu";

    public static void NavigateTO(string scene)
    {
        SceneManager.LoadScene(scene);
    }
}
