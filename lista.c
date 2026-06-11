#include <stdio.h>
#include <stdlib.h>

struct No {
    int info;
    struct No* proximo;
};

struct No* inserirNoInicio(struct No* inicio, int valor) {
    struct No* novoNo = (struct No*)malloc(sizeof(struct No));
    novoNo->info = valor;
    novoNo->proximo = inicio;
    return inicio;
}

struct No* inserirNoFinal(struct No* inicio, int valor) {
    struct No* novoNo = (struct No*)malloc(sizeof(struct No));
    novoNo->info = valor;
    novoNo->proximo = NULL;

    if (inicio == NULL) {
        return novoNo;
    }

    struct No* aux = inicio;
    while (aux->proximo != NULL) {
        aux = aux->proximo;
    }
    aux->proximo = novoNo;
    return inicio;
}

struct No* inserirNoMeio(struct No* inicio, int valor, int posicao) {
    struct No* novoNo = (struct No*)malloc(sizeof(struct No));
    novoNo->info = valor;

    if (posicao <= 1 || inicio == NULL) {
        novoNo->proximo = inicio;
        return novoNo;
    }

    struct No* aux = inicio;
    for (int i = 1; i < posicao - 1 && aux->proximo != NULL; i++) {
        aux = aux->proximo;
    }

    novoNo->proximo = aux->proximo;
    aux->proximo = novoNo;
    return inicio;
}

struct No* removerNoInicio(struct No* inicio) {
    if (inicio == NULL) {
        printf("Lista vazia.\n");
        return NULL;
    }
    struct No* aux = inicio;
    inicio = inicio->proximo;
    free(aux);
    return inicio;
}

struct No* removerNoFinal(struct No* inicio) {
    if (inicio == NULL) {
        printf("Lista vazia.\n");
        return NULL;
    }
    if (inicio->proximo == NULL) {
        free(inicio);
        return NULL;
    }
    struct No* aux = inicio;
    while (aux->proximo->proximo != NULL) {
        aux = aux->proximo;
    }
    free(aux->proximo);
    aux->proximo = NULL;
    return inicio;
}

struct No* removerNoMeio(struct No* inicio, int valor) {
    if (inicio == NULL) {
        printf("Lista vazia.\n");
        return NULL;
    }
    if (inicio->info == valor) {
        struct No* aux = inicio;
        inicio = inicio->proximo;
        free(aux);
        return inicio;
    }
    struct No* aux = inicio;
    while (aux->proximo != NULL && aux->proximo->info != valor) {
        aux = aux->proximo;
    }
    if (aux->proximo == NULL) {
        printf("Elemento nao encontrado.\n");
    } else {
        struct No* paraDeletar = aux->proximo;
        aux->proximo = aux->proximo->proximo;
        free(paraDeletar);
    }
    return inicio;
}

void imprimirLista(struct No* inicio) {
    if (inicio == NULL) {
        printf("Lista vazia.\n");
        return;
    }
    struct No* aux = inicio;
    while (aux != NULL) {
        printf("%d -> ", aux->info);
        aux = aux->proximo;
    }
    printf("NULL\n");
}

int main() {
    struct No* inicio = NULL;
    int opcao, valor, posicao;

    do {
        printf("\n--- MENU LISTA ENCADEADA ---\n");
        printf("1. Inserir no Inicio\n");
        printf("2. Inserir no Final\n");
        printf("3. Inserir no Meio (Por Posicao)\n");
        printf("4. Remover no Inicio\n");
        printf("5. Remover no Final\n");
        printf("6. Remover no Meio (Por Valor)\n");
        printf("7. Mostrar Lista\n");
        printf("0. Sair\n");
        printf("Escolha uma opcao: ");
        scanf("%d", &opcao);

        switch (opcao) {
            case 1:
                printf("Digite o valor: ");
                scanf("%d", &valor);
                inicio = inserirNoInicio(inicio, valor);
                break;
            case 2:
                printf("Digite o valor: ");
                scanf("%d", &valor);
                inicio = inserirNoFinal(inicio, valor);
                break;
            case 3:
                printf("Digite o valor: ");
                scanf("%d", &valor);
                printf("Digite a posicao (A partir de 1): ");
                scanf("%d", &posicao);
                inicio = inserirNoMeio(inicio, valor, posicao);
                break;
            case 4:
                inicio = removerNoInicio(inicio);
                break;
            case 5:
                inicio = removerNoFinal(inicio);
                break;
            case 6:
                printf("Digite o valor a ser removido: ");
                scanf("%d", &valor);
                inicio = removerNoMeio(inicio, valor);
                break;
            case 7:
                imprimirLista(inicio);
                break;
            case 0:
                printf("Saindo...\n");
                break;
            default:
                printf("Opcao invalida.\n");
        }
    } while (opcao != 0);

    struct No* aux;
    while (inicio != NULL) {
        aux = inicio;
        inicio = inicio->proximo;
        free(aux);
    }

    return 0;
}