export function useUserImage(user) {
  const getProfileImage = () =>
    user?.profileImage
      ? `http://localhost:8080/images/profile/${user.profileImage}`
      : new URL('@/assets/image/default_user.png', import.meta.url).href;

  const getBackgroundImage = () =>
    user?.backgroundImage
      ? `http://localhost:8080/images/background/${user.backgroundImage}`
      : new URL('@/assets/image/default_background.png', import.meta.url).href;

  return { getProfileImage, getBackgroundImage };
}
