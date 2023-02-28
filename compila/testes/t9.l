/* Exemplo de Programa na linguagem L */

integer n=-1;
string nome, sobrn, idade;
boolean naoTerminou, terminou=true;
const MAXITER=10;

/* Programa Principal */
main
  write('Digite seu nome: ');
  readln(nome);
  write('Digite seu sobrenome: ');
  readln(sobrn);
  write('Digite sua idade: ');
  readln(idade);
  /*write('Digite sua idade: ');
  readln(nome);*/
  naoTerminou=true;
  n=0;
  while (naoTerminou) begin
    writeln('Ola'' ',nome);
    n=n+1;
    naoTerminou=n<MAXITER;
  end
end
