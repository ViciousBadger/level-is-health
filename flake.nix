{
  inputs = {
    nixpkgs.url = "nixpkgs/nixos-23.11";
  };
  outputs = {
    self,
    nixpkgs,
    flake-utils,
  }: let
    lib = nixpkgs.lib;
    system = "x86_64-linux";
    pkgs = import nixpkgs {inherit system;};
    libraryPath = lib.makeLibraryPath (with pkgs; [
      libGL
    ]);
  in
    flake-utils.lib.eachDefaultSystem (system: {
      formatter = pkgs.alejandra;

      devShells.default = pkgs.mkShell rec {
        buildInputs = with pkgs; [
          jdk17
          libGL
        ];
        LD_LIBRARY_PATH = "${nixpkgs.lib.makeLibraryPath buildInputs}";
        # LD_LIBRARY_PATH="/run/opengl-driver/lib:/run/opengl-driver-32/lib";
      };
    });
}

