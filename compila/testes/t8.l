/* Exemplo de Programa na linguagem L */

integer n=-1;
string nome;
boolean naoTerminou;
const MAXITER=10;

/* Programa Principal */
main
  write('Digite seu nome: ');
  readln(nome);
  naoTerminou=true;
  n=0;
  while (naoTerminou) begin
    writeln('Ola'' ',nome);
    n=n+1;
    naoTerminou=n<MAXITER;
  end
end
